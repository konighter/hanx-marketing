package com.hzltd.module.erplus.adv.auth.service;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.adv.model.AdsTokenResult;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapter;
import com.hzltd.module.erplus.adv.adapter.AdsPlatformAdapterFactory;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountPageReqVO;
import com.hzltd.module.erplus.adv.auth.vo.AdsAccountVO;
import com.hzltd.module.erplus.adv.auth.vo.AdsAuthCallbackReqVO;
import com.hzltd.module.erplus.adv.auth.vo.AdsAuthUrlReqVO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountCredentialDO;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAccountDO;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountCredentialMapper;
import com.hzltd.module.erplus.adv.dal.mysql.AdsAccountMapper;
import com.hzltd.module.erplus.adv.dal.redis.AdsAuthStateRedisDAO;
import com.hzltd.module.erplus.adv.auth.service.dto.AdsAuthStateDTO;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.*;
import static com.hzltd.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 广告授权 Service 实现类
 */
@Slf4j
@Service
@Validated
public class AdsAuthServiceImpl implements AdsAuthService {

    @Resource
    private AdsAccountMapper adsAccountMapper;
    @Resource
    private AdsAccountCredentialMapper adsAccountCredentialMapper;
    @Resource
    private AdsPlatformAdapterFactory adsPlatformAdapterFactory;
    @Resource
    private AdsAuthStateRedisDAO adsAuthStateRedisDAO;

    @Override
    public String getAuthorizeUrl(AdsAuthUrlReqVO reqVO) {
        AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(reqVO.getPlatform());

        // 生成并存储 State
        String state = reqVO.getState();
        if (StrUtil.isEmpty(state)) {
            state = IdUtil.fastSimpleUUID();
        }
        adsAuthStateRedisDAO.set(state, AdsAuthStateDTO.builder()
                .platform(reqVO.getPlatform())
                .shopId(reqVO.getShopId())
                .userId(getLoginUserId())
                .build());

        return adapter.getAuthorizeUrl(state);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleCallback(AdsAuthCallbackReqVO reqVO) {
        // 校验 State
        AdsAuthStateDTO stateDTO = adsAuthStateRedisDAO.get(reqVO.getState());
        if (stateDTO == null) {
            throw exception(ADS_AUTH_CALLBACK_ERROR, "无效的 State 或已过期");
        }
        // 如果请求中有 platform，校验是否一致
        if (StrUtil.isNotEmpty(reqVO.getPlatform()) && !reqVO.getPlatform().equals(stateDTO.getPlatform())) {
            throw exception(ADS_AUTH_CALLBACK_ERROR, "平台信息不匹配");
        }

        String platform = stateDTO.getPlatform();
        AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(platform);

        // 1. 交换 Token
        AdsTokenResult tokenResult;
        try {
            tokenResult = adapter.exchangeToken(reqVO.getCode());
        } catch (Exception e) {
            throw exception(ADS_AUTH_CALLBACK_ERROR, e.getMessage());
        }

        // 2. 存储凭证 (由于没有 accountId 关联，这里根据平台和外部 ID 尝试查找已存在的凭证)
        // 注意：这里需要一个逻辑来定位凭证，或者先创建凭证再关联
        String externalAccountId = tokenResult.getExternalAccountId();

        // 尝试通过现有账号查找关联的 credentialId
        Long existingCredentialId = null;
        if (StrUtil.isNotEmpty(externalAccountId)) {
            AdsAccountDO existingAccount = adsAccountMapper.selectByPlatformAndExternalId(platform, externalAccountId);
            if (existingAccount != null) {
                existingCredentialId = existingAccount.getCredentialId();
            }
        }

        AdsAccountCredentialDO credential = null;
        if (existingCredentialId != null) {
            credential = adsAccountCredentialMapper.selectById(existingCredentialId);
        }

        if (credential == null) {
            credential = new AdsAccountCredentialDO();
            credential.setCredentialType("OAUTH2");
            // 设置一些标识性的信息在 extCredential 中？
        }

        credential.setAccessToken(tokenResult.getAccessToken());
        credential.setRefreshToken(tokenResult.getRefreshToken());
        credential.setTokenExpiresAt(tokenResult.getExpiresAt());
        credential.setPlatform(platform);

        if (credential.getId() == null) {
            adsAccountCredentialMapper.insert(credential);
        } else {
            adsAccountCredentialMapper.updateById(credential);
        }

        // 3. 调用初始化钩子 (处理账号发现逻辑)
        this.initAccount(credential, stateDTO.getShopId());

        // 4. 账号处理完后的 Hook (这里如果 initAccount 内部创建了账号，我们可能需要重新获取账号列表来触发 Hook)
        // 或者由 initAccount 内部触发 Hook。根据用户要求：调用 initAccount 来处理账号的逻辑，账号处理完加一个 postAccountAction 的 hook
        // 我们假设 initAccount 负责创建/更新账号，并设置 parentId / credentialId
        List<AdsAccountDO> accounts = adsAccountMapper.selectListByCredentialId(credential.getId());
        for (AdsAccountDO account : accounts) {
            adapter.postAccountAction(account);
        }

        // 5. 清理 State
        adsAuthStateRedisDAO.delete(reqVO.getState());

    }

    private void initAccount(AdsAccountCredentialDO credential, Long shopId) {
        AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(credential.getPlatform());
        List<AdsAccountVO> accountList = adapter.fetchAdsAccounts(credential, shopId);
        if (CollUtil.isEmpty(accountList)) {
            return;
        }

        // save or update account，根据extendAccountId查询
        for (AdsAccountVO vo : accountList) {
            AdsAccountDO account = adsAccountMapper.selectByPlatformAndExternalId(credential.getPlatform(), vo.getExternalAccountId());
            if (account == null) {
                account = new AdsAccountDO();
                account.setPlatform(credential.getPlatform());
                account.setExternalAccountId(vo.getExternalAccountId());
                account.setCredentialId(credential.getId());
                account.setShopId(shopId);
                account.setName(vo.getName());
                account.setCurrency(vo.getCurrency());
                account.setTimezone(vo.getTimezone());
                account.setAuthStatus(1);
                account.setExtConfig(JsonUtils.toJsonString(vo.getPlatformSpec()));
                account.setLastSyncedAt(LocalDateTime.now());
                adsAccountMapper.insert(account);
                log.info("[initAccount][{}] 新增广告账号: {} ({})", credential.getPlatform(), vo.getName(), vo.getExternalAccountId());
            } else {
                account.setCredentialId(credential.getId());
                account.setShopId(shopId);
                account.setName(vo.getName());
                account.setCurrency(vo.getCurrency());
                account.setTimezone(vo.getTimezone());
                account.setAuthStatus(1);
                account.setExtConfig(JsonUtils.toJsonString(vo.getPlatformSpec()));
                account.setLastSyncedAt(LocalDateTime.now());
                adsAccountMapper.updateById(account);
                log.info("[initAccount][{}] 更新广告账号: {} ({})", credential.getPlatform(), vo.getName(), vo.getExternalAccountId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshToken(Long accountId) {
        AdsAccountDO account = adsAccountMapper.selectById(accountId);
        if (account == null) {
            throw exception(ADS_ACCOUNT_NOT_EXISTS);
        }

        Long credentialId = account.getCredentialId();
        if (credentialId == null && account.getParentId() != null) {
            // 尝试通过父账号找
            AdsAccountDO parentAccount = adsAccountMapper.selectById(account.getParentId());
            if (parentAccount != null) {
                credentialId = parentAccount.getCredentialId();
            }
        }

        if (credentialId == null) {
            throw new IllegalStateException("账号未关联凭证，无法刷新");
        }

        AdsAccountCredentialDO credential = adsAccountCredentialMapper.selectById(credentialId);
        if (credential == null) {
            throw new IllegalStateException("凭证记录不存在，无法刷新");
        }

        AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(account.getPlatform());
        AdsTokenResult tokenResult = adapter.refreshToken(credential);

        credential.setAccessToken(tokenResult.getAccessToken());
        if (tokenResult.getRefreshToken() != null) {
            credential.setRefreshToken(tokenResult.getRefreshToken());
        }
        credential.setTokenExpiresAt(tokenResult.getExpiresAt());
        adsAccountCredentialMapper.updateById(credential);
    }

    @Override
    public List<AdsAccountDO> getAccountList() {
        // 授权列表页展示所有顶级账号 (parentId 为空) 或 按照业务需求
        return adsAccountMapper.selectList();
    }

    @Override
    public PageResult<AdsAccountDO> getAccountPage(AdsAccountPageReqVO reqVO) {
        return adsAccountMapper.selectPage(reqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAccount(Long id) {
        AdsAccountDO account = adsAccountMapper.selectById(id);
        if (account == null) {
            throw exception(ADS_ACCOUNT_NOT_EXISTS);
        }

        // 1. 删除账号记录
        adsAccountMapper.deleteById(id);

        // 2. 级联处理？如果这是唯一的指向该凭证的账号，考虑删除凭证
        // TODO: 完善级联删除逻辑
    }

    @Override
    public AdsAccountDO getAccount(Long id) {
        return adsAccountMapper.selectById(id);
    }

    @Override
    public AdsAccountCredentialDO getAccountCredentialByAccount(Long accountId) {
        AdsAccountDO accountDO = this.getAccount(accountId);
        if (accountDO == null) {
            return null;
        }

        // 如果账号没有关联凭证, 检查父账号, 如果父账号也没有关联, 返回Null(默认只有2级账号)
        Long credentialId = accountDO.getCredentialId();
        if (credentialId == null && accountDO.getParentId() != null) {
            AdsAccountDO parentAccount = getAccount(accountDO.getParentId());
            if (parentAccount != null) {
                credentialId = parentAccount.getCredentialId();
            }
        }

        if (credentialId == null) {
            return null;
        }

        AdsAccountCredentialDO accountCredential = adsAccountCredentialMapper.selectById(credentialId);

        if (accountCredential != null) {
            // check expireTime
            if (accountCredential.getTokenExpiresAt() != null &&
                    accountCredential.getTokenExpiresAt().plusSeconds(60).isBefore(LocalDateTime.now())) {
                AdsPlatformAdapter adapter = adsPlatformAdapterFactory.getAdapter(accountDO.getPlatform());
                AdsTokenResult tokenResult = adapter.refreshToken(accountCredential);

                accountCredential.setAccessToken(tokenResult.getAccessToken());
                if (tokenResult.getRefreshToken() != null) {
                    accountCredential.setRefreshToken(tokenResult.getRefreshToken());
                }
                accountCredential.setTokenExpiresAt(tokenResult.getExpiresAt());
                adsAccountCredentialMapper.updateById(accountCredential);
            }
        }
        return accountCredential;
    }
}
