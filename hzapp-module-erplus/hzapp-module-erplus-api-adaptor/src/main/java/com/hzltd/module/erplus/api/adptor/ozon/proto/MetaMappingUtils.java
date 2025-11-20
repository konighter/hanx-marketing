package com.hzltd.module.erplus.api.adptor.ozon.proto;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.api.adptor.ozon.OzonCategoryService;
import com.hzltd.module.erplus.constant.AttributeTypeEnum;
import com.hzltd.module.erplus.constant.LanguageEnum;
import com.hzltd.module.erplus.model.category.AttributeValueModel;
import com.hzltd.module.erplus.model.category.CategoryAttributeModel;
import com.hzltd.module.erplus.model.category.CategoryModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class MetaMappingUtils {

    public static String mapLanguage(LanguageEnum language) {
        return switch (language) {
            case ZH_CN -> "ZH_HANS";
            default -> "EN";
        };
    }

    public static List<CategoryModel> mapCategoryList(List<OzonCategoryService.CategoryRespModel> categoryRespModels, String parentCategoryId) {

        List<CategoryModel> categoryModels = new LinkedList<>();
        for (OzonCategoryService.CategoryRespModel categoryRespModel : categoryRespModels) {
            CategoryModel categoryModel = new CategoryModel();
            categoryModel.setCategoryId(String.valueOf(categoryRespModel.getDescriptionCategoryId()));
            categoryModel.setName(categoryRespModel.getCategoryName());
            categoryModel.setLeaf(categoryRespModel.getChildren() == null || categoryRespModel.getChildren().isEmpty());
            categoryModel.setParentCategoryId(parentCategoryId);
            categoryModels.add(categoryModel);
            if (CollectionUtils.isNotEmpty(categoryRespModel.getChildren())) {
                categoryModels.addAll(mapCategoryList(categoryRespModel.getChildren(), categoryModel.getCategoryId()));
            } else {
                categoryModel.setCategoryId(categoryRespModel.getTypeId());
                categoryModel.setName(categoryRespModel.getTypeName());
            }
        }

        return categoryModels;
    }


    public static List<CategoryAttributeModel> mapCategoryAttributeList(List<OzonCategoryService.CategoryAttributeRespModel> result, Function<OzonCategoryService.CategoryAttributeRespModel, List<OzonCategoryService.CategoryAttributeValueRespModel>> mapper) {


       return result.stream().map(attri -> {
            CategoryAttributeModel categoryAttributeModel = new CategoryAttributeModel();
            categoryAttributeModel.setAttrCode(attri.getId());
            categoryAttributeModel.setAttrName(attri.getName());
            categoryAttributeModel.setAttrDescription(attri.getDescription());
            categoryAttributeModel.setAttrType(attri.getIsAspect() ? AttributeTypeEnum.SALES_PROPERTY : AttributeTypeEnum.PRODUCT_PROPERTY);
           categoryAttributeModel.setFieldType(attri.getType());
           categoryAttributeModel.setGroupName(attri.getGroupName());
            categoryAttributeModel.setIsRequired(attri.getIsRequired());
            categoryAttributeModel.setIsMulSelection(attri.getIsCollection());
            categoryAttributeModel.setIsCommon(!attri.getCategoryDependent());
            categoryAttributeModel.setIsCustomizable(attri.getDescription().contains("手动指定")); // todo -- ai判断
            categoryAttributeModel.setExtra(JsonUtils.toJsonString(attri));

            if (attri.getDictionaryId() != null && attri.getDictionaryId() > 0L) {
                List<OzonCategoryService.CategoryAttributeValueRespModel> categoryAttributeValueRespModels = mapper.apply(attri);
                categoryAttributeModel.setOptions(categoryAttributeValueRespModels.stream().map(item -> {
                    AttributeValueModel attributeValueModel = new AttributeValueModel();
                    attributeValueModel.setValue(item.getId());
                    attributeValueModel.setValueName(item.getValue());
                    return attributeValueModel;
                }).collect(Collectors.toList()));

            }
            return categoryAttributeModel;


        }).collect(Collectors.toUnmodifiableList());
    }

}
