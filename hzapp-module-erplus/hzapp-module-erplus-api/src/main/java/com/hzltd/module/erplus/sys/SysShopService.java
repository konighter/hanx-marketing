package com.hzltd.module.erplus.sys;

import com.hzltd.module.erplus.sys.domain.ShopVO;

public interface SysShopService {

    /**
     * @param shopId 本地ShopId
     * @return
     */
    ShopVO getShopById(Long shopId);

    /**
     * @param shopId 跨境平台的shopId
     * @return
     */
    ShopVO getShopByExtraId(String shopId);


}
