package com.hzltd.module.erplus.dal.dataobject.categoryattr;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 品类属性 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_category_attribute")
@KeySequence("erplus_category_attribute_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAttributeDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Integer id;
    /**
     * 属性ID
     */
    private String attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 品类ID
     */
    private Integer categoryId;
    /**
     * 属性类型
     */
    private Integer attrType;
    /**
     * 是否必填
     */
    private boolean required;
    /**
     * 是否公共属性
     */
    private boolean isCommon;

}