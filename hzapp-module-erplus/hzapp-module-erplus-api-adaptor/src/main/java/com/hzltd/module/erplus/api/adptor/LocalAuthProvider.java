package com.hzltd.module.erplus.api.adptor;

import com.hzltd.module.erplus.model.ApiRequest;
import com.hzltd.module.erplus.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.sys.SystemShopService;
import com.hzltd.module.erplus.sys.model.ShopModel;
import jakarta.annotation.Resource;

public class LocalAuthProvider {

    @Resource
    private SystemShopService systemShopService;


    public AuthorizationModel getAuthorizationModel(ApiRequest<?> request) {
       ShopModel shopModel = systemShopService.getShopByExtraId(request.getShopId());
        return shopModel.getAuthInfo();
    }

}
