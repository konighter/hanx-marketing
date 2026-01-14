package com.hzltd.module.erplus.dal.dataobject.cross;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 商品属性 DO
 *
 * @author 翰展科技
 */
@TableName("erplus_cross_product_attrs")
@KeySequence("erplus_cross_product_attrs_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrossProductAttrsDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 属性ID
     */
    private String attrId;
    /**
     * 属性名
     */
    private String attrName;
    /**
     * 属性值
     */
    private String attrValue;


}