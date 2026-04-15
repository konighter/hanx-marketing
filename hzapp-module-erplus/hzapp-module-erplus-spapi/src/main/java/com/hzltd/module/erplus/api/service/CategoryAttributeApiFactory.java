package com.hzltd.module.erplus.api.service;

import com.hzltd.module.erplus.api.adapter.AbsCrossApiServiceFactory;
import com.hzltd.module.erplus.spapi.service.category.CategoryAttributeMappingApi;
import org.springframework.stereotype.Service;

@Service
public class CategoryAttributeApiFactory extends AbsCrossApiServiceFactory<CategoryAttributeMappingApi> {
    @Override
    public Class<CategoryAttributeMappingApi> getCrossApiServiceClass() {
        return CategoryAttributeMappingApi.class;
    }
}
