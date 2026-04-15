package com.hzltd.module.amz.spapi.service.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormFieldVO;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Strategy for flattening Amazon JSON Schema properties into UI fields.
 */
public interface PropertyFlattenStrategy {

    /**
     * Returns true if this strategy supports the given product type.
     */
    boolean supports(String productType);

    /**
     * Flattens the schema root into a list of UI form fields.
     */
    List<AmzListingFormFieldVO> flatten(JsonNode schemaRoot, FlattenContext ctx);

    @Data
    class FlattenContext {
        private String productType;
        private Map<String, String> fieldMapping;
        private Map<String, AmzListingFormFieldVO> bizPathToFieldMap = new java.util.HashMap<>();
        private Set<String> importantFields;
        
        public FlattenContext(String productType, Map<String, String> fieldMapping) {
            this.productType = productType;
            this.fieldMapping = fieldMapping;
        }
    }
}
