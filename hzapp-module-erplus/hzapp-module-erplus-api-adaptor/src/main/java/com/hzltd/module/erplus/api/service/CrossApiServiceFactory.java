package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.service.category.CategoryApi;
import com.hzltd.module.erplus.service.product.ProductApi;
import org.springframework.stereotype.Service;

@Service
public class CrossApiServiceFactory {

    public ProductApi getProductApi(CrossPlatformEnum platform) {
        return null;
    }

    public CategoryApi getCategoryApi(CrossPlatformEnum platform) {
        return null;
    }


}
