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
        this.processProperties(properties, "", "", fields, ctx);

        return fields;
    }

    private void processProperties(JsonNode properties, String formPrefix, String bizPrefix, List<AmzListingFormFieldVO> fields, FlattenContext ctx) {
        Iterator<Map.Entry<String, JsonNode>> it = properties.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            String key = entry.getKey();
            JsonNode propSchema = entry.getValue();
            
            // 1. 过滤技术字段：marketplace_id 和 language_tag 不显式渲染
            if ("marketplace_id".equals(key) || "language_tag".equals(key)) {
                log.debug("[DefaultPropertyFlattenStrategy] Filtering technical field: {}", key);
                continue;
            }

            String currentFormPath = StrUtil.isEmpty(formPrefix) ? key : formPrefix + "." + key;
            String currentBizPath = StrUtil.isEmpty(bizPrefix) ? key : bizPrefix + "." + key;

            // Determine if this property should be represented as a UI field
            if (shouldCreateField(key, propSchema)) {
                // Determine if we should collapse this array for the UI
                boolean isArray = "array".equals(propSchema.path("type").asText());
                int maxItems = propSchema.path("maxItems").asInt(Integer.MAX_VALUE);
                int maxUniqueItems = propSchema.path("maxUniqueItems").asInt(Integer.MAX_VALUE);
                boolean shouldCollapseArray = isArray && (maxItems == 1 || maxUniqueItems == 1);

                // If collapsing, the bizField points to the first element (.0)
                String fieldBizPath = shouldCollapseArray ? currentBizPath + ".0" : currentBizPath;
                
                AmzListingFormFieldVO field = createField(currentFormPath, fieldBizPath, propSchema);
                fields.add(field);
                
                // If it's a composite (nested object or object array), process its children into compositeAttributes
                if (Boolean.TRUE.equals(field.getIsComposite())) {
                    processCompositeChildren(propSchema, currentFormPath, fieldBizPath, field, ctx);
                }
            } else if (propSchema.has("properties")) {
                // If it's a structural container without a specific field ID, keep diving
                processProperties(propSchema.get("properties"), currentFormPath, currentBizPath, fields, ctx);
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

    private AmzListingFormFieldVO createField(String formPath, String bizPath, JsonNode schema) {
        AmzListingFormFieldVO field = new AmzListingFormFieldVO();
        field.setId(formPath);
        field.setFormField(formPath); // 前端渲染标识
        field.setBizField(bizPath);   // 原始业务路径项 (包含 .0)
        field.setTitle(schema.path("title").asText(formPath));
        field.setDescription(schema.path("description").asText(""));
        field.setType(schema.path("type").asText("object"));
        
        // Complex type identification
        boolean isObject = "object".equals(field.getType());
        boolean isArray = "array".equals(field.getType());

        if (isObject && schema.has("properties")) {
            field.setIsComposite(true);
        } else if (isArray) {
            JsonNode items = schema.get("items");
            int maxItems = schema.path("maxItems").asInt(Integer.MAX_VALUE);
            int maxUniqueItems = schema.path("maxUniqueItems").asInt(Integer.MAX_VALUE);
            
            if ((maxItems == 1 || maxUniqueItems == 1) && items != null) {
                // Optimization: treat arrays that can only have 1 item as single objects/scalars
                log.debug("[DefaultPropertyFlattenStrategy] Collapsing array for form: {}, biz: {}", formPath, bizPath);
                String itemType = items.path("type").asText("object");
                field.setType(itemType);
                if ("object".equals(itemType)) {
                    field.setIsComposite(true);
                } else {
                    this.extractOptions(items, field);
                }
            } else if (items != null) {
                if ("object".equals(items.path("type").asText())) {
                    field.setIsComposite(true);
                } else {
                    // Scalar array: check for enums in items
                    this.extractOptions(items, field);
                }
            }
        }

        // Leaf node: extract enums if present
        this.extractOptions(schema, field);

        return field;
    }

    private void extractOptions(JsonNode schema, AmzListingFormFieldVO field) {
        if (schema.has("enum")) {
            JsonNode enums = schema.get("enum");
            JsonNode names = schema.get("enumNames");
            List<AmzListingFormFieldVO.Option> options = new ArrayList<>();
            for (int i = 0; i < enums.size(); i++) {
                String value = enums.get(i).asText();
                String label = (names != null && names.has(i)) ? names.get(i).asText() : value;
                options.add(new AmzListingFormFieldVO.Option(label, value));
            }
            if (!options.isEmpty()) {
                field.setOptions(options);
                // If it's a leaf node and has options, treat it as enum type for UI rendering
                if (!Boolean.TRUE.equals(field.getIsComposite())) {
                    field.setType("enum");
                }
            }
        } else if (schema.has("oneOf") || schema.has("anyOf")) {
            // Handle oneOf/anyOf pattern with const and title
            JsonNode cases = schema.has("oneOf") ? schema.get("oneOf") : schema.get("anyOf");
            List<AmzListingFormFieldVO.Option> options = new ArrayList<>();
            boolean hasOpenString = false;
            
            for (JsonNode c : cases) {
                if (c.has("const")) {
                    String value = c.get("const").asText();
                    String label = c.path("title").asText(value);
                    options.add(new AmzListingFormFieldVO.Option(label, value));
                } else if (c.has("enum")) {
                    // Nested enum in anyOf/oneOf
                    JsonNode enums = c.get("enum");
                    JsonNode names = c.get("enumNames");
                    for (int i = 0; i < enums.size(); i++) {
                        String value = enums.get(i).asText();
                        String label = (names != null && names.has(i)) ? names.get(i).asText() : value;
                        options.add(new AmzListingFormFieldVO.Option(label, value));
                    }
                } else if ("string".equals(c.path("type").asText()) && !c.has("enum") && !c.has("const")) {
                    // Branch allows arbitrary string input
                    hasOpenString = true;
                }
            }
            
            if (!options.isEmpty()) {
                field.setOptions(options);
                field.setType("enum");
                if (hasOpenString) {
                    field.setAllowCustomEnum(true);
                }
            }
        }
    }

    private void processCompositeChildren(JsonNode schema, String formPath, String bizPath, AmzListingFormFieldVO parentField, FlattenContext ctx) {
        List<AmzListingFormFieldVO> children = new ArrayList<>();
        
        JsonNode subProps = null;
        if (schema.has("properties")) {
            subProps = schema.get("properties");
        } else if (schema.has("items") && schema.get("items").has("properties")) {
            subProps = schema.get("items").get("properties");
        }

        if (subProps != null) {
            processProperties(subProps, formPath, bizPath, children, ctx);
            // In the real implementation, AmzListingFormFieldVO would wrap CategoryAttributeModel 
            // or we'd map them directly. For V2, we ensure the hierarchy is preserved in the VO.
            parentField.setChildren(children);
        }
    }
}
