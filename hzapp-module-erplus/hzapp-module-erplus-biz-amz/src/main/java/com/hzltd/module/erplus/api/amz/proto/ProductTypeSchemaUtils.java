package com.hzltd.module.erplus.api.adptor.amz.proto;

import com.google.common.collect.Maps;
import com.hzltd.framework.common.util.http.HttpUtils;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.common.util.spring.SpringUtils;
import com.hzltd.module.erplus.api.adptor.amz.mapping.AmzAttributeSchemaMappingService;
import com.hzltd.module.erplus.model.category.AttributeValueModel;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import org.apache.commons.collections4.CollectionUtils;
import software.amazon.spapi.models.producttypedefinitions.v2020_09_01.ProductTypeDefinition;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductTypeSchemaUtils {

    public static String getProductTypeSchema(String linkUrl) {
        return HttpUtils.get(linkUrl, null);
    }


    public static List<CategoryAttributeModel> parseProductTypeSchema(String schemaLink, ProductTypeDefinition productTypeDefinition) {
        Map<String, String> propertyGroups = Maps.newHashMap();
        productTypeDefinition.getPropertyGroups().forEach((key, group) -> {
            group.getPropertyNames().forEach(property -> {
                propertyGroups.put(property, key);
            });
        });

        String productTypeSchema = getProductTypeSchema(schemaLink);
        ProductTypeSchema schemaObject = JsonUtils.parseObject(productTypeSchema, ProductTypeSchema.class);

        if (schemaObject == null || schemaObject.getProperties() == null) {
            return Collections.emptyList();
        }

        List<String> requiredAttributes = schemaObject.getRequired();

        return schemaObject.getProperties().entrySet().stream().map(entry -> {
            CategoryAttributeModel model = new CategoryAttributeModel();
            model.setAttrCode(entry.getKey());
            model.setAttrName(entry.getValue().getTitle());
            model.setAttrDescription(entry.getValue().getDescription());
            model.setGroupName(propertyGroups.get(entry.getKey()));
            model.setFieldType(entry.getValue().getType());

            // 处理枚举值
            if (CollectionUtils.isNotEmpty(entry.getValue().getEnumNames())) {
                model.setIsCustomizable(false);
                dealWithEnumValues(model, entry.getValue());
            } else {
                model.setIsCustomizable(true);
            }

            model.setIsRequired(requiredAttributes.contains(entry.getKey()));
            model.setIsEditable(entry.getValue().getEditable());
            model.setIsMulSelection(entry.getValue().getMaxUniqueItems() != null && entry.getValue().getMaxUniqueItems() > 1);
            model.setExtra(JsonUtils.toJsonString(entry.getValue()));

            // 处理特殊属性
            SpringUtils.getBean(AmzAttributeSchemaMappingService.class).doMapAttributeSchema(model, entry.getValue());

            return model;
        }).collect(Collectors.toList());

    }

    private static void dealWithEnumValues(CategoryAttributeModel model, ProductTypeSchemaItem item) {
        for (int i = 0; i < item.getEnumValues().size(); i++) {
            AttributeValueModel valueModel = new AttributeValueModel();
            valueModel.setValue(item.getEnumValues().get(i));
            valueModel.setValueName(item.getEnumNames().get(i));
            model.getOptions().add(valueModel);
        }
    }

}
