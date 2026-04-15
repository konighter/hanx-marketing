package com.hzltd.module.erplus.system.service;

import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.system.model.PlatformAccountModel;

public interface SystemAuthService {

    /**
     * 根据店铺和授权范围获取授权模型
     * 优先返回当前用户的授权，如果没有则返回该店铺的默认授权
     *
     * @param shopId    店铺ID
     * @param authType 授权范围
     * @return 授权模型
     */
    AuthorizationModel getAuthorizationModel(Long shopId, String authType);

    void grantShopAuth(Long authId, Long shopId);


    /**
     * 获得平台账号
     *
     * @param id 编号
     * @return 平台账号
     */
    PlatformAccountModel getPlatformAccount(Long id);

    /**
     * 获取或创建平台账号
     *
     * @param account 平台账号信息
     * @return 平台账号
     */
    PlatformAccountModel getOrCreatePlatformAccount(PlatformAccountModel account);

}
