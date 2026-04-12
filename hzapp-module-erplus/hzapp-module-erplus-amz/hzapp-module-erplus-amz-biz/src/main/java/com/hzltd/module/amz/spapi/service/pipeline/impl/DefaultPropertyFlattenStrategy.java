package com.hzltd.module.amz.spapi.service.pipeline.impl;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormFieldVO;
import com.hzltd.module.amz.spapi.service.pipeline.PropertyFlattenStrategy;
import com.hzltd.module.erplus.spapi.model.category.CategoryAttributeModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Default implementation of the PropertyFlattenStrategy.
 * Implements a recursive traversal that intelligently identifies core business components 
 * (like 'purchasable_offer' or 'dimensions') and preserves their nested structure 
 * instead of flattening them into scalar dot-notated fields.
 */
@Slf4j
@Component
public class DefaultPropertyFlattenStrategy implements PropertyFlattenStrategy {

    @Override
    public boolean supports(String productType) {
        return true; // Default fallback for all types
    }

    @Override
    public List<AmzListingFormFieldVO> flatten(JsonNode schemaRoot, FlattenContext ctx) {
        List<AmzListingFormFieldVO> fields = new ArrayList<>();
        JsonNode properties = schemaRoot.get("properties");
        if (properties == null) return fields;

        // Amazon Listing schemas often wrap all actual fields in an 'attributes' object.
        // To match our UI metadata (which expects base IDs like 'purchasable_offer'), 
        // we skip the 'attributes.' prefix if it's the top-level container.
        if (properties.has("attributes") && properties.get("attributes").has("properties")) {
            log.info("[DefaultPropertyFlattenStrategy] Skipping 'attributes' top-level wrapper for cleaner IDs");
            properties = properties.get("attributes").get("properties");
        }

        // Start recursion from the identified properties root
        this.processProperties(properties, "", fields, ctx);

        return fields;
    }

    private void processProperties(JsonNode properties, String prefix, List<AmzListingFormFieldVO> fields, FlattenContext ctx) {
        Iterator<Map.Entry<String, JsonNode>> it = properties.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            String key = entry.getKey();
            JsonNode propSchema = entry.getValue();
            String fullPath = StrUtil.isEmpty(prefix) ? key : prefix + "." + key;

            // Determine if this property should be represented as a UI field
            if (shouldCreateField(key, propSchema)) {
                AmzListingFormFieldVO field = createField(fullPath, propSchema);
                fields.add(field);
                
                // If it's a composite (nested object or object array), process its children into compositeAttributes
                if (Boolean.TRUE.equals(field.getIsComposite())) {
                    processCompositeChildren(propSchema, fullPath, field, ctx);
                }
            } else if (propSchema.has("properties")) {
                // If it's a structural container without a specific field ID, keep diving
                processProperties(propSchema.get("properties"), fullPath, fields, ctx);
            }
        }
    }

    private boolean shouldCreateField(String key, JsonNode schema) {
        // Create a field if it has a type (leaf or array) OR if it's a known complex business unit
        // Usually, Amazon schemas have 'type' at the level they want data submitted.
        return schema.has("type") || schema.has("$ref") || isKnownBusinessUnit(key);
    }

    private boolean isKnownBusinessUnit(String key) {
        // Avoid over-flattening these specific structures
        return "purchasable_offer".equals(key) || "item_dimensions".equals(key);
    }

    private AmzListingFormFieldVO createField(String path, JsonNode schema) {
        AmzListingFormFieldVO field = new AmzListingFormFieldVO();
        field.setId(path);
        field.setTitle(schema.path("title").asText(path));
        field.setDescription(schema.path("description").asText(""));
        field.setType(schema.path("type").asText("object"));
        
        // Complex type identification
        boolean isObject = "object".equals(field.getType());
        boolean isArray = "array".equals(field.getType());

        if (isObject && schema.has("properties")) {
            field.setIsComposite(true);
        } else if (isArray) {
            JsonNode items = schema.get("items");
            if (items != null && "object".equals(items.path("type").asText())) {
                field.setIsComposite(true);
            }
        }

        return field;
    }

    private void processCompositeChildren(JsonNode schema, String path, AmzListingFormFieldVO parentField, FlattenContext ctx) {
        List<AmzListingFormFieldVO> children = new ArrayList<>();
        
        JsonNode subProps = null;
        if (schema.has("properties")) {
            subProps = schema.get("properties");
        } else if (schema.has("items") && schema.get("items").has("properties")) {
            subProps = schema.get("items").get("properties");
        }

        if (subProps != null) {
            processProperties(subProps, path, children, ctx);
            // In the real implementation, AmzListingFormFieldVO would wrap CategoryAttributeModel 
            // or we'd map them directly. For V2, we ensure the hierarchy is preserved in the VO.
            parentField.setChildren(children);
        }
    }
}
