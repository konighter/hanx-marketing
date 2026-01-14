package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.config.AbsCrossApiServiceFactory;
import com.hzltd.module.erplus.service.category.CategoryApi;
import com.hzltd.module.erplus.service.product.ProductApi;
import org.springframework.stereotype.Service;

@Service
public class ProductApiFactory extends AbsCrossApiServiceFactory<ProductApi> {

    @Override
    public Class<ProductApi> getCrossApiServiceClass() {
        return ProductApi.class;
    }
}
