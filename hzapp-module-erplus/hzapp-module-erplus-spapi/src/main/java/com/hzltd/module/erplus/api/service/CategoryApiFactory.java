package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.config.AbsCrossApiServiceFactory;
import com.hzltd.module.spapi.service.category.CategoryApi;

import org.springframework.stereotype.Service;

@Service
public class CategoryApiFactory extends AbsCrossApiServiceFactory<CategoryApi> {
    @Override
    public Class<CategoryApi> getCrossApiServiceClass() {
        return CategoryApi.class;
    }
}
