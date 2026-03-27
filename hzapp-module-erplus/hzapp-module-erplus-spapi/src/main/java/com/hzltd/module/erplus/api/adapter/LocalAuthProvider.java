package com.hzltd.module.erplus.api.adapter;

import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.system.service.SystemAuthService;
import com.hzltd.module.system.service.SystemShopService;
import com.hzltd.module.system.model.ShopModel;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.NotImplementedException;

public abstract class LocalAuthProvider {

    @Resource
    private SystemShopService systemShopService;

    @Resource
    private SystemAuthService systemAuthService;


    public AuthorizationModel getAuthorizationModel(ApiRequest<?> request) {
        ShopModel shopModel = systemShopService.getShopByExtraId(request.getShopId());
        if (shopModel == null) {
            return null;
        }
        return systemAuthService.getAuthorizationModel(Long.valueOf(shopModel.getId()), getAuthScope());
    }

    public abstract String getAuthScope();

    public abstract String getAuthEndpoint();

    public abstract String getApiEndpoint(String marketPlaceId);

    protected RefreshTokenCacheAdaptor getTokenCache() {
        throw new NotImplementedException();
    };

}
