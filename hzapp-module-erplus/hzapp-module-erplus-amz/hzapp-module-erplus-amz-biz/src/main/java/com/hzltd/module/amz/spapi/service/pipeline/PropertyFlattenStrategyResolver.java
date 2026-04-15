package com.hzltd.module.amz.spapi.service.pipeline;

import com.hzltd.module.amz.spapi.service.pipeline.impl.DefaultPropertyFlattenStrategy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Resolves the appropriate flattening strategy for a given product type.
 */
@Component
public class PropertyFlattenStrategyResolver {

    @Resource
    private List<PropertyFlattenStrategy> strategies;

    @Resource
    private DefaultPropertyFlattenStrategy defaultStrategy;

    public PropertyFlattenStrategy resolve(String productType) {
        return strategies.stream()
                .filter(s -> s.supports(productType))
                .findFirst()
                .orElse(defaultStrategy);
    }
}
