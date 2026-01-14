package com.hzltd.module.erplus.api.adptor;

import com.amazon.SellingPartnerAPIAA.LWAAccessTokenCacheImpl;
import com.amazon.SellingPartnerAPIAA.LWAAuthorizationCredentials;
import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.sys.SystemShopService;
import com.hzltd.module.erplus.sys.model.ShopModel;
import com.hzltd.module.infra.dal.dataobject.config.ConfigDO;
import com.hzltd.module.infra.service.config.ConfigService;
import jakarta.annotation.Resource;

public abstract class LocalAuthProvider {

    @Resource
    private SystemShopService systemShopService;


    public AuthorizationModel getAuthorizationModel(ApiRequest<?> request) {
       ShopModel shopModel = systemShopService.getShopByExtraId(request.getShopId());
        return shopModel.getAuthInfo();
    }

    public LWAAuthorizationCredentials getLWAAuthorizationCredentials(AuthorizationModel authorizationModel) {
        return LWAAuthorizationCredentials.builder()
                .clientId(authorizationModel.getAppKey())
                .clientSecret(authorizationModel.getAppSecret())
                .refreshToken(authorizationModel.getRefreshToken())
                .endpoint(this.getAuthEndpoint())
                .build();
    }

    public abstract String getAuthEndpoint();

    public abstract String getApiEndpoint(String marketPlaceId);

    public abstract RefreshTokenCacheAdaptor getTokenCache();

}
