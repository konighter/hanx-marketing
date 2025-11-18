package com.hzltd.module.erplus.model.category;

import com.hzltd.module.erplus.constant.AttributeTypeEnum;
import lombok.Data;

import java.util.List;

@Data
public class CategoryAttributeModel {

    /**
     * 属性ID
     */
    private String attrCode;

    /**
     * 属性名
     */
    private String attrName;

    /**
     * 属性描述, 用于提示用户输入
     */
    private String attrDescription;

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
    private List<AttributeValueModel> options;

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

    /**
     * 是否通用属性
     */
    private boolean isCommon;

    /**
     * 扩展信息
     */
    private String extra;



}
