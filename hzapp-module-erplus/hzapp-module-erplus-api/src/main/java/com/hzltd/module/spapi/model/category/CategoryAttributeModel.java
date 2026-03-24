package com.hzltd.module.spapi.model.category;

import com.hzltd.module.spapi.enums.AttributeTypeEnum;
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
     * 属性值
     */
    private AttributeValueModel attrValue;

    /**
     * 属性分组名
     */
    private String groupName;

    /**
     * 属性字段类型
     */
    private String fieldType;

    /**
     * 是否可编辑
     */
    private Boolean isEditable;

    /**
     * 是否必填
     */
    private Boolean isRequired;

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
    private Boolean isMulSelection;

    /**
     * 是否支持自定义
     */
    private Boolean isCustomizable;

    /**
     * 是否通用属性
     */
    private Boolean isCommon;

    /**
     * 扩展信息
     */
    private String extra;

    /**
     * 是否聚合属性
     */
    private Boolean isComposite;

     /**
     * 聚合属性值
     */
    private List<CategoryAttributeModel> compositeAttributes;



    public void setValue(Object attrValue) {
        if (attrValue instanceof AttributeValueModel) {
            this.attrValue = (AttributeValueModel) attrValue;
        } else if (attrValue instanceof String) {
            this.attrValue = AttributeValueModel.of((String) attrValue, (String) attrValue);
        }
    }


}
