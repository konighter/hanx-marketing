package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.adapter.AbsCrossApiServiceFactory;
import com.hzltd.module.spapi.service.product.ProductApi;
import org.springframework.stereotype.Service;

@Service
public class ProductApiFactory extends AbsCrossApiServiceFactory<ProductApi> {

    @Override
    public Class<ProductApi> getCrossApiServiceClass() {
        return ProductApi.class;
    }
}
