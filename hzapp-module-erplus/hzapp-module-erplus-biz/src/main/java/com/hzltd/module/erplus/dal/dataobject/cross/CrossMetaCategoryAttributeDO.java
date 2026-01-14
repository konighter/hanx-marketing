package com.hzltd.module.erplus.dal.dataobject.cross;

import lombok.*;
import com.baomidou.mybatisplus.annotation.*;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;

/**
 * 跨境平台品类属性 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_meta_category_attribute")
@KeySequence("erplus_cross_meta_category_attribute_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossMetaCategoryAttributeDO extends BaseDO {

    /**
     * ID
     */
    @TableId
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