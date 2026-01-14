package com.hzltd.module.erplus.service.listing;

import com.hzltd.module.erplus.controller.admin.listing.vo.ProductListingReqVO;
import com.hzltd.module.erplus.service.spu.ProductSkuService;
import com.hzltd.module.erplus.service.spu.ProductSpuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.*;

@Service
public class ProductListingServiceImpl implements ProductListingService {

    @Resource
    private ProductSpuService spuService;

    @Resource
    private ProductSkuService skuService;

    @Override
    public void submitListing(ProductListingReqVO reqVO) {
        if (!validListing(reqVO)) {
            throw exception(LISTING_NOT_VALID);
        }






    }


    private boolean validListing(ProductListingReqVO reqVO) {
        return true;
    }


}
