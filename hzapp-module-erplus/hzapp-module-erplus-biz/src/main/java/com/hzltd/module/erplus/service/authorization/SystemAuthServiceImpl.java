package com.hzltd.module.erplus.service.authorization;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.api.service.AuthorizationApiFactory;
import com.hzltd.module.erplus.dal.dataobject.authorization.PlatformAuthDO;
import com.hzltd.module.erplus.dal.dataobject.authorization.ShopAuthDO;
import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAccountDO;
import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAppDO;
import com.hzltd.module.erplus.dal.mysql.authorization.PlatformAuthMapper;
import com.hzltd.module.erplus.dal.mysql.authorization.ShopAuthMapper;
import com.hzltd.module.erplus.dal.mysql.shop.PlatformAccountMapper;
import com.hzltd.module.erplus.service.shop.PlatformAppService;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModelV0;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.PlatformAccountModel;
import com.hzltd.module.erplus.system.service.SystemAuthService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hzltd.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 系统授权服务实现类
 */
@Service
public class SystemAuthServiceImpl implements SystemAuthService {

    /** key = "userId:shopId:authType", value = Optional<AuthorizationModel> */
    private final Cache<String, Optional<AuthorizationModel>> authModelCache = Caffeine.newBuilder()
            .expireAfter(new Expiry<String, Optional<AuthorizationModel>>() {
                @Override
                public long expireAfterCreate(String key, Optional<AuthorizationModel> value, long currentTime) {
                    if (value.isEmpty()) {
                        return TimeUnit.SECONDS.toNanos(2);
                    }
                    return TimeUnit.MINUTES.toNanos(45);
                }

                @Override
                public long expireAfterUpdate(String key, Optional<AuthorizationModel> value, long currentTime, long currentDuration) {
                    return expireAfterCreate(key, value, currentTime);
                }

                @Override
                public long expireAfterRead(String key, Optional<AuthorizationModel> value, long currentTime, long currentDuration) {
                    return currentDuration;
                }
            })
            .build();

    @Resource
    private ShopAuthMapper shopAuthMapper;
    @Resource
    private PlatformAuthMapper platformAuthMapper;
    @Resource
    private PlatformAppService platformAppService;
    @Resource
    private PlatformAccountMapper accountMapper;

    @Resource
    private AuthorizationApiFactory authorizationApiFactory;

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

    @Override
    public AuthorizationModel getAuthorizationModel(Long shopId, String authType) {
        Long userId = getLoginUserId();

        // 构建缓存 key
        String cacheKey = (userId != null ? userId : "anon") + ":" + shopId + ":" + authType;
        Optional<AuthorizationModel> opt = authModelCache.getIfPresent(cacheKey);
        if (opt != null) {
            return opt.orElse(null);
        }

        // 缓存未命中或已过期，从 DB 查询
        AuthorizationModel model = loadAuthorizationModel(shopId, authType, userId);

        // 写入缓存（null 也缓存，通过 Expiry 控制过期时间）
        authModelCache.put(cacheKey, Optional.ofNullable(model));

        return model;
    }

    private AuthorizationModel loadAuthorizationModel(Long shopId, String authType, Long userId) {
        // 1. 优先查询当前用户的授权
        PlatformAuthDO authDO = null;
        if (userId != null) {
            authDO = platformAuthMapper.selectListByShopIdAndUserId(shopId, userId).stream()
                    .filter(a -> authType.equals(a.getAuthType()))
                    .findFirst()
                    .orElse(null);
        }

        // 2. 如果不存在，查询默认授权
        if (authDO == null) {
            authDO = platformAuthMapper.selectDefaultAuthByShopIdAndType(shopId, authType);
        }
        // 判断RefreshToken是否过期
        if (authDO == null) {
            return null;
        }

        // 3. 转换为 AuthorizationModel 并填充 App 信息
        AuthorizationModel model = BeanUtils.toBean(authDO, AuthorizationModel.class);
        model.setShopId(shopId);
        PlatformAppDO app = platformAppService.getPlatformApp(authDO.getAppId());
        if (app != null) {
            model.setAppKey(app.getAppKey());
            model.setAppSecret(app.getAppSecret());
        }
// 如果过期了, 更新
        if (model.isExpiry()) {

            AuthorizationModelV0 accessTokenModel = authorizationApiFactory.getCrossApiService(CrossPlatformEnum.of(authDO.getPlatform()))
                    .refreshAccessToken(AuthorizationModelV0.builder()
                            .appKey(model.getAppKey())
                            .appSecret(model.getAppSecret())
                            .refreshToken(model.getRefreshToken())
                            .authType(model.getAuthType()).build());
            if (accessTokenModel == null) {
                return null;
            }
            model.setAccessToken(accessTokenModel.getAccessToken());
            model.setExpiryTime(LocalDateTime.now().plusSeconds(accessTokenModel.getExpireIn()));
        }

        return model;
    }
}
