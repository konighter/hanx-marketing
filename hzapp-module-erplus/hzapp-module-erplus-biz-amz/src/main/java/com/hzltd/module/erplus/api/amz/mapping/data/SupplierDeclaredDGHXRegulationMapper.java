package com.hzltd.module.erplus.api.adptor.amz.mapping.data;

import com.google.common.collect.Lists;
import com.hzltd.module.erplus.api.adptor.amz.mapping.AttributeSchemaMapper;
import com.hzltd.module.erplus.api.adptor.amz.mapping.AttributeValueMapper;
import com.hzltd.module.erplus.api.adptor.amz.proto.ProductTypeSchemaItem;
import com.hzltd.module.erplus.model.category.AttributeValueModel;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.sys.model.ProductSpuModel;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

public class SupplierDeclaredDGHXRegulationMapper implements AttributeValueMapper, AttributeSchemaMapper {
    private static final List<String> ruleEnum = List.of("ghs","not_applicable","storage","other","waste","unknown","transportation");

    private static final List<String> ruleEnumName = List.of("GHS","不适用","储存","其他","废物","未知","运输");

    @Override
    public void mapAttributeValue(ProductSpuModel spuModel, CategoryAttributeModel categoryAttributeModel) {
        if (CollectionUtils.isEmpty(categoryAttributeModel.getOptions())) {
            categoryAttributeModel.setOptions(Lists.newArrayList());
            for (int i = 0; i < ruleEnum.size(); i++) {
                categoryAttributeModel.getOptions().add(AttributeValueModel.of(ruleEnum.get(i), ruleEnumName.get(i)));
            }
        }

    }

    @Override
    public void mapAttributeSchema(CategoryAttributeModel categoryAttributeModel, ProductTypeSchemaItem schemaItem) {
        // 处理country_of_origin的options
        if (CollectionUtils.isEmpty(categoryAttributeModel.getOptions())) {
            ProductTypeSchemaItem valueSchemaItem = schemaItem.getItems().getProperties().get("value");
            categoryAttributeModel.setOptions(Lists.newArrayList());
            for (int i = 0; i < valueSchemaItem.getEnumValues().size(); i++) {
                categoryAttributeModel.getOptions().add(AttributeValueModel.of(valueSchemaItem.getEnumValues().get(i), valueSchemaItem.getEnumNames().get(i)));
            }
        }
    }

    @Override
    public String getAttribute() {
        return "supplier_declared_dg_hz_regulation";
    }
}
