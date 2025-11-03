package com.hzltd.module.erplus.model.category;

import com.hzltd.module.erplus.constant.AttributeTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class CategoryAttributeModel {

    /**
     * 属性ID
     */
    private String attrId;

    /**
     * 属性名
     */
    private String attrName;

    /**
     * 属性类型
     */
    private AttributeTypeEnum attrType;

    /**
     * 是否必填
     */
    private boolean isRequired;

    /**
     * 可选值
     */
    private List<AttributeValueModel> selectValues;

    /**
     * 满足任一条件, 本属性必填
     */
    private List<AttributeConditionModel> requireConditions;

    /**
     * 是否多选
     */
    private boolean isMulSelection;

    /**
     * 是否支持自定义
     */
    private boolean isCustomizable;



}
