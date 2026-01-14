package com.hzltd.module.erplus.convert.category;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.constant.AttributeTypeEnum;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryAttributeVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryAttrReqVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryReqVO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryAttributeDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryDO;
import com.hzltd.module.erplus.model.category.*;
import org.apache.commons.collections4.CollectionUtils;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CrossCategoryConvert {
    CrossCategoryConvert INSTANCE = Mappers.getMapper(CrossCategoryConvert.class);

    CategoryVO toCategoryVO(CategoryModel model);

    default CategoryVO toCategoryVO(CrossMetaCategoryDO metaCategoryDO) {
        CategoryVO categoryModel = new CategoryVO();
        categoryModel.setCategoryId(metaCategoryDO.getCategoryCode());
        categoryModel.setName(metaCategoryDO.getCategoryName());
        categoryModel.setParentCategoryId(metaCategoryDO.getParentCategoryCode());
        categoryModel.setLeaf(metaCategoryDO.getLeaf());
        return categoryModel;
    }

    default CrossMetaCategoryDO toMetaCategoryDO(CategoryModel categoryVO) {
        CrossMetaCategoryDO categoryDO = new CrossMetaCategoryDO();
        categoryDO.setCategoryCode(categoryVO.getCategoryId());
        categoryDO.setCategoryName(categoryVO.getName());
        categoryDO.setParentCategoryCode(categoryVO.getParentCategoryId());
        categoryDO.setLeaf(categoryVO.isLeaf());
        return categoryDO;
    }

    CategoryAttributeVO toCategoryAttrVO(CategoryAttributeModel model);

    default GetCategoryRequest toGetCategoryRequest(CrossCategoryReqVO request) {
        GetCategoryRequest getCategoryRequest = new GetCategoryRequest();
        getCategoryRequest.setName(request.getName());
        if (CollectionUtils.isNotEmpty(request.getShopIds())) {
            getCategoryRequest.setShopId(request.getShopIds().get(0));
        }

        return getCategoryRequest;
    };

   default CategoryAttributeVO toCategoryAttributeVO(CrossMetaCategoryAttributeDO crossMetaCategoryAttributeDO) {
       if ( crossMetaCategoryAttributeDO == null ) {
           return null;
       }

       CategoryAttributeVO categoryAttributeVO = new CategoryAttributeVO();

       categoryAttributeVO.setAttrCode( crossMetaCategoryAttributeDO.getAttrCode() );
       categoryAttributeVO.setAttrName( crossMetaCategoryAttributeDO.getAttrName() );
       categoryAttributeVO.setAttrDescription( crossMetaCategoryAttributeDO.getAttrDescription() );
       if ( crossMetaCategoryAttributeDO.getAttrType() != null ) {
           categoryAttributeVO.setAttrType( AttributeTypeEnum.values()[ crossMetaCategoryAttributeDO.getAttrType() ] );
       }
       categoryAttributeVO.setGroupName( crossMetaCategoryAttributeDO.getGroupName() );
       categoryAttributeVO.setFieldType( crossMetaCategoryAttributeDO.getFieldType() );

       categoryAttributeVO.setOptions(JsonUtils.parseArray(crossMetaCategoryAttributeDO.getAttrOptions(), AttributeValueModel.class));

       categoryAttributeVO.setIsRequired( crossMetaCategoryAttributeDO.getIsRequired() );
       categoryAttributeVO.setIsMulSelection( crossMetaCategoryAttributeDO.getIsMulSelection() );
       categoryAttributeVO.setIsCustomizable( crossMetaCategoryAttributeDO.getIsCustomizable() );
       categoryAttributeVO.setIsCommon( crossMetaCategoryAttributeDO.getIsCommon() );
       categoryAttributeVO.setIsEditable( crossMetaCategoryAttributeDO.getIsEditable() );

       return categoryAttributeVO;
   }

    default GetCategoryAttributeRequest toGetCategoryAttributeRequest(CrossCategoryAttrReqVO request) {
        GetCategoryAttributeRequest getCategoryAttributeRequest = new GetCategoryAttributeRequest();
        getCategoryAttributeRequest.setCategoryIds(request.getCategoryIds());
        if (CollectionUtils.isNotEmpty(request.getCategoryIds())) {
            getCategoryAttributeRequest.setCategoryId(request.getCategoryIds().get(request.getCategoryIds().size() -1));
        }
        return getCategoryAttributeRequest;
    }

    default CrossMetaCategoryAttributeDO toCategoryAttributeDO(CategoryAttributeModel categoryAttributeModel) {
        CrossMetaCategoryAttributeDO crossMetaCategoryAttributeDO = new CrossMetaCategoryAttributeDO();
        crossMetaCategoryAttributeDO.setAttrCode(categoryAttributeModel.getAttrCode());
        crossMetaCategoryAttributeDO.setAttrName(categoryAttributeModel.getAttrName());
        crossMetaCategoryAttributeDO.setAttrDescription(categoryAttributeModel.getAttrDescription());
        if (categoryAttributeModel.getAttrType() != null) {
            crossMetaCategoryAttributeDO.setAttrType(categoryAttributeModel.getAttrType().getValue());
        }
        crossMetaCategoryAttributeDO.setAttrOptions(JsonUtils.toJsonString(categoryAttributeModel.getOptions()));
        crossMetaCategoryAttributeDO.setFieldType(categoryAttributeModel.getFieldType());
        crossMetaCategoryAttributeDO.setGroupName(categoryAttributeModel.getGroupName());
        crossMetaCategoryAttributeDO.setIsEditable(categoryAttributeModel.getIsEditable());
        crossMetaCategoryAttributeDO.setIsMulSelection(categoryAttributeModel.getIsMulSelection());
        crossMetaCategoryAttributeDO.setIsRequired(categoryAttributeModel.getIsRequired());
        crossMetaCategoryAttributeDO.setIsCustomizable(categoryAttributeModel.getIsCustomizable());
        crossMetaCategoryAttributeDO.setIsCommon(categoryAttributeModel.getIsCommon());
        crossMetaCategoryAttributeDO.setExtra(categoryAttributeModel.getExtra());
        return crossMetaCategoryAttributeDO;
    }
}
