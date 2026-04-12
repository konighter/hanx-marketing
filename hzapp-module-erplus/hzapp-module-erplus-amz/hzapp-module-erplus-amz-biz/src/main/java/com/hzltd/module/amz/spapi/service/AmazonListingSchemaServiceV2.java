package com.hzltd.module.amz.spapi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormConfigVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormFieldVO;
import com.hzltd.module.amz.spapi.service.pipeline.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * V2 implementation of Amazon Listing Schema Service.
 * Follows the 4-stage pipeline architecture for better maintainability and performance.
 */
@Slf4j
@Service
public class AmazonListingSchemaServiceV2 {

    @Resource
    private ListingSchemaLoader schemaLoader;

    @Resource
    private PropertyFlattenStrategyResolver flattenResolver;

    @Resource
    private LinkageRuleResolver ruleResolver;

    @Resource
    private UiOverlayMerger uiMerger;

    /**
     * Generates a form configuration for a specific product type using the new V2 pipeline.
     */
    public AmzListingFormConfigVO generateFormConfig(String productType) {
        log.info("[AmazonListingSchemaServiceV2] Generating form config for productType: {}", productType);
        
        // Stage 1: Load Schema (Caffeine Cache)
        JsonNode schemaRoot = schemaLoader.loadSchema(productType);
        if (schemaRoot == null) {
            log.warn("[AmazonListingSchemaServiceV2] Schema not found for productType: {}", productType);
            return null;
        }

        // Stage 2: Structural Transformation (Smart Property Flattening)
        // We use a strategy resolver to decide how to flatten the schema for this productType.
        PropertyFlattenStrategy.FlattenContext context = new PropertyFlattenStrategy.FlattenContext(productType, new HashMap<>());
        PropertyFlattenStrategy strategy = flattenResolver.resolve(productType);
        List<AmzListingFormFieldVO> fields = strategy.flatten(schemaRoot, context);

        // Stage 3: Dynamic Linkage Resolution (Visitor Pattern)
        // Parses if-then-else rules into the standardized LogicExpressionVO for the frontend.
        ruleResolver.resolve(schemaRoot, fields);

        // Stage 4: UI Overlay & Grouping (Reference Lingxing layout)
        // Merges UI hints, grouping, and ordering per productType.
        uiMerger.merge(productType, fields);

        // Final Assembly
        AmzListingFormConfigVO config = new AmzListingFormConfigVO();
        config.setProductType(productType);
        config.setFields(fields);
        config.setFieldMapping(context.getFieldMapping());
        
        return config;
    }
}
