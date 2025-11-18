package com.hzltd.module.erplus.convert.category;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.constant.AttributeTypeEnum;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryAttributeVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CategoryVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryAttrReqVO;
import com.hzltd.module.erplus.controller.admin.category.vo.CrossCategoryReqVO;
import com.hzltd.module.erplus.dal.dataobject.category.CrossMetaCategoryAttributeDO;
import com.hzltd.module.erplus.dal.dataobject.category.CrossMetaCategoryDO;
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
       categoryAttributeVO.setOptions(JsonUtils.parseArray(crossMetaCategoryAttributeDO.getAttrOptions(), AttributeValueModel.class));
       categoryAttributeVO.setRequired( crossMetaCategoryAttributeDO.isRequired() );
       categoryAttributeVO.setMulSelection( crossMetaCategoryAttributeDO.isMulSelection() );
       categoryAttributeVO.setCustomizable( crossMetaCategoryAttributeDO.isCustomizable() );
       categoryAttributeVO.setCommon( crossMetaCategoryAttributeDO.isCommon() );

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
        crossMetaCategoryAttributeDO.setAttrType(categoryAttributeModel.getAttrType().getValue());
        crossMetaCategoryAttributeDO.setAttrOptions(JsonUtils.toJsonString(categoryAttributeModel.getOptions()));
        crossMetaCategoryAttributeDO.setMulSelection(categoryAttributeModel.isMulSelection());
        crossMetaCategoryAttributeDO.setRequired(categoryAttributeModel.isRequired());
        crossMetaCategoryAttributeDO.setCommon(categoryAttributeModel.isCommon());
        return crossMetaCategoryAttributeDO;
    }
}
