package com.hzltd.module.system.service;

import com.hzltd.module.erplus.model.shop.PlatformAccountModel;

public interface SystemAuthService {

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
