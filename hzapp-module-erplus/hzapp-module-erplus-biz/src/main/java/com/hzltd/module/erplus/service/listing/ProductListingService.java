package com.hzltd.module.erplus.service.listing;

import com.hzltd.module.amz.spapi.controller.admin.listing.vo.ProductListingReqVO;

public interface ProductListingService {
    /**
     * 提交刊登任务
     * @param reqVO
     */
     void submitListing(ProductListingReqVO reqVO);
}
