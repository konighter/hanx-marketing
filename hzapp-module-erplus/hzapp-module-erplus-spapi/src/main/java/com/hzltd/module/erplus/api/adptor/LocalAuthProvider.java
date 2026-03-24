package com.hzltd.module.erplus.api.adptor;

import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.system.service.SystemShopService;
import com.hzltd.module.spapi.model.system.ShopModel;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.NotImplementedException;

public abstract class LocalAuthProvider {

    @Resource
    private SystemShopService systemShopService;


    public AuthorizationModel getAuthorizationModel(ApiRequest<?> request) {
       ShopModel shopModel = systemShopService.getShopByExtraId(request.getShopId());
        return shopModel.getAuthInfo();
    }

    public abstract String getAuthEndpoint();

    public abstract String getApiEndpoint(String marketPlaceId);

    protected RefreshTokenCacheAdaptor getTokenCache() {
        throw new NotImplementedException();
    };

}
