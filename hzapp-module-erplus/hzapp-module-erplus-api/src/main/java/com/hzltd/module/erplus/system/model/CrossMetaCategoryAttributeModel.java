package com.hzltd.module.erplus.system.model;

import lombok.Data;

@Data
public class CrossMetaCategoryAttributeModel {
    /**
     * ID
     */
    private Integer id;
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
     * 品类code
     */
    private String categoryCode;
    /**
     * 平台ID
     */
    private Integer platformId;
    /**
     * 属性类型
     */
    private Integer attrType;
    /**
     * 属性可选值
     */
    private String attrOptions;

    /**
     * 属性分组
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
}
