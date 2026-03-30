package com.hzltd.module.erplus.service.authorization;

import com.hzltd.framework.tenant.core.context.TenantContextHolder;
import com.hzltd.framework.tenant.core.util.TenantUtils;
import com.hzltd.module.erplus.controller.admin.authorization.vo.PlatformAuthGenerateReqVO;
import com.hzltd.module.erplus.controller.admin.authorization.vo.PlatformAuthSubmitReqVO;
import com.hzltd.module.erplus.dal.dataobject.authorization.PlatformAuthDO;
import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAppDO;
import com.hzltd.module.erplus.service.shop.PlatformAppService;
import com.hzltd.module.erplus.dal.mysql.authorization.PlatformAuthMapper;
import com.hzltd.module.spapi.model.authorization.AuthorizationModelV0;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.system.enums.OAuthGrantTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hzltd.module.erplus.dal.redis.authorization.PlatformAuthStateRedisDAO;
import com.hzltd.module.erplus.service.authorization.dto.AuthStateDTO;
import jakarta.annotation.Resource;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

import static com.hzltd.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 平台授权业务服务实现类
 */
@Slf4j
@Service
public class PlatformAuthServiceImpl implements PlatformAuthService {

    @Resource
    private AuthInternalService authInternalService;

    @Resource
    private PlatformAuthMapper platformAuthMapper;

    @Resource
    private PlatformAppService platformAppService;

    @Resource
    private PlatformAuthStateRedisDAO platformAuthStateRedisDAO;

    @Override
    public String generateAuthUrl(PlatformAuthGenerateReqVO reqVO) {
        // 1. 生成唯一的 state (UUID)
        String state = UUID.randomUUID().toString().replace("-", "");
        
        // 2. 构建 AuthStateDTO 并存入 Redis
        AuthStateDTO stateDTO = AuthStateDTO.builder()
                .userId(getLoginUserId())
                .tenantId(TenantContextHolder.getTenantId())
                .appId(reqVO.getAppId())
                .authType(reqVO.getAuthType())
                .platform(reqVO.getPlatform().name())
                .region(reqVO.getRegion())
                .build();
        platformAuthStateRedisDAO.set(state, stateDTO);

        return authInternalService.getAuthLink(reqVO.getPlatform(), reqVO.getAppId(), reqVO.getRegion(), reqVO.getAuthType(), state);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAuth(PlatformAuthSubmitReqVO reqVO) {
        if (Boolean.TRUE.equals(reqVO.getSelfAuth())) {
            // 获取应用凭证
            PlatformAppDO app = platformAppService.getPlatformApp(reqVO.getAppId());
            if (app == null) {
                throw new RuntimeException("Platform application not found: " + reqVO.getAppId());
            }

            // 自授权流程：直接使用 refreshToken 换取 accessToken 并保存
            AuthorizationModelV0 model = authInternalService.getAccessToken(
                    reqVO.getPlatform(), reqVO.getRegion(), reqVO.getAuthType(), reqVO.getRefreshToken(), OAuthGrantTypeEnum.REFRESH_TOKEN,
                    app.getAppKey(), app.getAppSecret(), reqVO.getSellerId());

            PlatformAuthDO authDO = savePlatformAuth(reqVO.getPlatform(), reqVO.getRegion(), reqVO.getAuthType(), model, reqVO.getSellerId(), reqVO.getAppId(), getLoginUserId());

            // 2.4 初始化账号 (例如同步店铺信息)
            authInternalService.initAccount(authDO);
        } else {
            // 平台授权流程通常走回调
            throw new RuntimeException("Platform auth usually handled via callback. For self-auth, set selfAuth to true.");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleCallback(String authType, String code, String state) {
        // 1. 从 Redis 获取 state 信息
        AuthStateDTO stateDTO = platformAuthStateRedisDAO.get(state);
        if (stateDTO == null) {
            throw new RuntimeException("Invalid or expired state: " + state);
        }

        // 2. 在租户上下文中执行回调逻辑
        TenantUtils.execute(stateDTO.getTenantId(), () -> {
            // 2.1 获取应用凭证
            PlatformAppDO app = platformAppService.getPlatformApp(stateDTO.getAppId());
            if (app == null) {
                throw new RuntimeException("Platform application not found for appId: " + stateDTO.getAppId());
            }

            // 2.2 执行授权换码
            CrossPlatformEnum platform = CrossPlatformEnum.valueOf(stateDTO.getPlatform());
            String region = stateDTO.getRegion();
            String finalAuthType = authType != null ? authType : stateDTO.getAuthType();
            finalAuthType = finalAuthType.toUpperCase();

            AuthorizationModelV0 model = authInternalService.getAccessToken(platform, region, finalAuthType, code, OAuthGrantTypeEnum.AUTHORIZATION_CODE,
                    app.getAppKey(), app.getAppSecret(), null);

            // 2.3 保存授权信息
            PlatformAuthDO authDO = savePlatformAuth(platform, region, finalAuthType, model, model.getSellerId(), stateDTO.getAppId(), stateDTO.getUserId());
            
            // 2.4 初始化账号 (例如同步店铺信息)
            authInternalService.initAccount(authDO);

        });
        // 2.5 成功后删除 state
        platformAuthStateRedisDAO.delete(state);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refreshAuth(Long shopId) {
        Long userId = getLoginUserId();
        // 1. 获取该店铺属于当前用户的授权
        List<PlatformAuthDO> auths = platformAuthMapper.selectListByShopIdAndUserId(shopId, userId);
        if (auths == null || auths.isEmpty()) {
            return;
        }

        // 2. 遍历并刷新每一个授权
        for (PlatformAuthDO authDO : auths) {
            if (authDO.getRefreshToken() == null) {
                continue;
            }

            // 2.1 获取应用凭证
            PlatformAppDO app = platformAppService.getPlatformApp(authDO.getAppId());
            if (app == null) {
                log.error("[refreshAuth][shopId({}) authId({})] 找不到绑定的 appId({})", shopId, authDO.getId(), authDO.getAppId());
                continue;
            }

            // 2.2 执行刷新
            try {
                AuthorizationModelV0 model = authInternalService.getAccessToken(
                        CrossPlatformEnum.valueOf(authDO.getPlatform()),
                        authDO.getRegion(),
                        authDO.getAuthType(),
                        authDO.getRefreshToken(),
                        OAuthGrantTypeEnum.REFRESH_TOKEN,
                        app.getAppKey(),
                        app.getAppSecret(),
                        authDO.getSellerId());

                // 2.3 更新保存
                authDO.setAccessToken(model.getAccessToken());
                if (model.getExpireIn() != null) {
                    authDO.setExpiryTime(LocalDateTime.now().plusSeconds(model.getExpireIn()));
                }
                platformAuthMapper.updateById(authDO);
            } catch (Exception e) {
                log.error("[refreshAuth][shopId({}) authId({})] 刷新失败", shopId, authDO.getId(), e);
            }
        }
    }

    private PlatformAuthDO savePlatformAuth(CrossPlatformEnum platform, String region, String authType, AuthorizationModelV0 model, String sellerId, Long appId, Long userId) {
        // 使用 selectByUserIdAndPlatformAndRegionAndSellerId 实现 saveOrUpdate 逻辑
        PlatformAuthDO authDO = platformAuthMapper.selectByUserIdAndPlatformAndRegionAndSellerId(
                userId, platform.name(), region, appId, sellerId != null ? sellerId : model.getSellerId());
        
        boolean isUpdate = authDO != null;
        if (!isUpdate) {
            authDO = new PlatformAuthDO();
            authDO.setPlatform(platform.name());
            authDO.setUserId(userId);
            authDO.setAuthScope(authType.toUpperCase());
            authDO.setAuthType(authType.toUpperCase());
            authDO.setRegion(region);
            authDO.setSellerId(sellerId != null ? sellerId : model.getSellerId());
            authDO.setAppId(appId);
        }

        authDO.setRefreshToken(model.getRefreshToken());
        authDO.setAccessToken(model.getAccessToken());

        if (model.getExpireIn() != null) {
            authDO.setExpiryTime(LocalDateTime.now().plusSeconds(model.getExpireIn()));
        }

        if (isUpdate) {
            platformAuthMapper.updateById(authDO);
        } else {
            platformAuthMapper.insert(authDO);
        }
        return authDO;
    }
}
