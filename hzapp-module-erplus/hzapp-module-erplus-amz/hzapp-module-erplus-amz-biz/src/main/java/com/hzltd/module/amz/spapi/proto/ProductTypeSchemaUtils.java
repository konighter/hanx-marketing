package com.hzltd.module.amz.spapi.proto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Maps;
import com.hzltd.framework.common.util.http.HttpUtils;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.spring.SpringUtils;
import com.hzltd.module.amz.spapi.mapping.AmzAttributeSchemaMappingService;
import com.hzltd.module.erplus.spapi.model.category.AttributeValueModel;
import com.hzltd.module.erplus.spapi.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.spapi.model.category.MetaCategorySchemaResult;
import org.apache.commons.collections4.CollectionUtils;
import software.amazon.spapi.models.producttypedefinitions.v2020_09_01.ProductTypeDefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductTypeSchemaUtils {

    public static String getProductTypeSchema(String linkUrl) {
        return HttpUtils.get(linkUrl, null);
    }

    /**
     * Parses the product type schema.
     * Returns a wrapper containing both processed attributes and the full raw schema.
     */
    public static MetaCategorySchemaResult parseProductTypeSchema(String schemaLink, ProductTypeDefinition productTypeDefinition) {
        String productTypeSchemaJson = getProductTypeSchema(schemaLink);
        if (productTypeSchemaJson == null) {
            return MetaCategorySchemaResult.builder()
                    .attributes(Collections.emptyList())
                    .fullSchema("")
                    .build();
        }

        // 1. Prepare Property Groups map
        Map<String, String> propertyGroups = Maps.newHashMap();
        if (productTypeDefinition.getPropertyGroups() != null) {
            productTypeDefinition.getPropertyGroups().forEach((key, group) -> {
                if (group.getPropertyNames() != null) {
                    group.getPropertyNames().forEach(property -> propertyGroups.put(property, key));
                }
            });
        }

        // 2. Parse into both Java Object (for logic) and JsonNode (for raw fragments)
        ProductTypeSchema schemaObject = JsonUtils.parseObject(productTypeSchemaJson, ProductTypeSchema.class);
        JsonNode rootNode = JsonUtils.parseTree(productTypeSchemaJson);

        if (schemaObject == null || schemaObject.getProperties() == null || !rootNode.has("properties")) {
            return MetaCategorySchemaResult.builder()
                    .attributes(Collections.emptyList())
                    .fullSchema(productTypeSchemaJson)
                    .build();
        }

        List<String> requiredAttributes = schemaObject.getRequired() != null ? schemaObject.getRequired() : Collections.emptyList();
        JsonNode propertiesNode = rootNode.get("properties");

        // 3. Process each attribute
        List<CategoryAttributeModel> attributeModels = schemaObject.getProperties().entrySet().stream().map(entry -> {
            String attrCode = entry.getKey();
            ProductTypeSchemaItem item = entry.getValue();
            
            CategoryAttributeModel model = new CategoryAttributeModel();
            model.setAttrCode(attrCode);
            model.setAttrName(item.getTitle());
            model.setAttrDescription(item.getDescription());
            model.setGroupName(propertyGroups.get(attrCode));
            model.setFieldType(item.getType());

            // Handle enums
            if (CollectionUtils.isNotEmpty(item.getEnumNames())) {
                model.setIsCustomizable(false);
                dealWithEnumValues(model, item);
            } else {
                model.setIsCustomizable(true);
            }

            model.setIsRequired(requiredAttributes.contains(attrCode));
            model.setIsEditable(item.getEditable());
            model.setIsMulSelection(item.getMaxUniqueItems() != null && item.getMaxUniqueItems() > 1);

            // Important: Store the raw JSON fragment for this specific attribute
            JsonNode attrSchemaNode = propertiesNode.get(attrCode);
            if (attrSchemaNode != null) {
                model.setExtra(JsonUtils.toJsonString(attrSchemaNode));
            }

            // Apply special mappings if any
            try {
                AmzAttributeSchemaMappingService mappingService = SpringUtils.getBean(AmzAttributeSchemaMappingService.class);
                if (mappingService != null) {
                    mappingService.doMapAttributeSchema(model, item);
                }
            } catch (Exception e) {
                // Ignore if bean doesn't exist or mapping fails
            }

            return model;
        }).collect(Collectors.toList());

        return MetaCategorySchemaResult.builder()
                .attributes(attributeModels)
                .fullSchema(productTypeSchemaJson)
                .build();
    }

    private static void dealWithEnumValues(CategoryAttributeModel model, ProductTypeSchemaItem item) {
        if (model.getOptions() == null) {
            model.setOptions(new ArrayList<>());
        }
        if (item.getEnumValues() != null && item.getEnumNames() != null) {
            int size = Math.min(item.getEnumValues().size(), item.getEnumNames().size());
            for (int i = 0; i < size; i++) {
                AttributeValueModel valueModel = new AttributeValueModel();
                valueModel.setValue(item.getEnumValues().get(i));
                valueModel.setValueName(item.getEnumNames().get(i));
                model.getOptions().add(valueModel);
            }
        }
    }
}
