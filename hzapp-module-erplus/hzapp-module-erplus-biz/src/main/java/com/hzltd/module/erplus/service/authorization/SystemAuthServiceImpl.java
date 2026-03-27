package com.hzltd.module.erplus.service.authorization;

import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.authorization.ShopAuthDO;
import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAccountDO;
import com.hzltd.module.erplus.dal.mysql.authorization.ShopAuthMapper;
import com.hzltd.module.erplus.dal.mysql.shop.PlatformAccountMapper;
import com.hzltd.module.erplus.model.shop.PlatformAccountModel;
import com.hzltd.module.system.service.SystemAuthService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;

/**
 * 系统授权服务实现类
 */
@Service
public class SystemAuthServiceImpl implements SystemAuthService {

    @Resource
    private ShopAuthMapper shopAuthMapper;
    @Resource
    private PlatformAccountMapper accountMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void grantShopAuth(Long authId, Long shopId) {
        // 1. 检查是否已经存在该关联
        ShopAuthDO shopAuth = shopAuthMapper.selectOne(new LambdaQueryWrapperX<ShopAuthDO>()
                .eq(ShopAuthDO::getShopId, shopId.intValue())
                .eq(ShopAuthDO::getAuthId, authId)
                .last("LIMIT 1"));

        if (shopAuth == null) {
            // 2. 创建关联记录
            shopAuth = new ShopAuthDO();
            shopAuth.setShopId(shopId.intValue());
            shopAuth.setAuthId(authId);

            // 3. 逻辑：如果该店铺尚无关联，则设为默认
            Long count = shopAuthMapper.selectCount(ShopAuthDO::getShopId, shopId.intValue());
            shopAuth.setIsDefault(count == 0);

            shopAuthMapper.insert(shopAuth);
        }
    }

    @Override
    public PlatformAccountModel getOrCreatePlatformAccount(PlatformAccountModel account) {
        if (account == null) {
            return null;
        }

        PlatformAccountDO accountDO = null;
        if (StringUtils.isNotEmpty(account.getRegistrationNumber())) {
            accountDO = getPlatformAccountByRegistrationNumber(account.getRegistrationNumber());
        } else if (StringUtils.isNotEmpty(account.getName())) {
            accountDO = accountMapper.selectOne(new LambdaQueryWrapperX<PlatformAccountDO>()
                    .eq(PlatformAccountDO::getName, account.getName()));
        }

        if (accountDO != null) {
            account.setId(accountDO.getId());
            return account;
        }

        // 创建账号
        accountDO = new PlatformAccountDO();
        accountDO.setPlatform(account.getPlatform())
                .setName(account.getName())
                .setRegistrationNumber(account.getRegistrationNumber())
                .setBusinessType(account.getBusinessType())
                .setAddress(account.getAddress());

        accountMapper.insert(accountDO);
        account.setId(accountDO.getId());
        return account;
    }

    private PlatformAccountDO getPlatformAccountByRegistrationNumber(String registrationNumber) {
        return accountMapper.selectOne(new LambdaQueryWrapperX<PlatformAccountDO>()
                .eq(PlatformAccountDO::getRegistrationNumber, registrationNumber));
    }

    @Override
    public PlatformAccountModel getPlatformAccount(Long id) {
        return BeanUtils.toBean(accountMapper.selectById(id), PlatformAccountModel.class);
    }
}
