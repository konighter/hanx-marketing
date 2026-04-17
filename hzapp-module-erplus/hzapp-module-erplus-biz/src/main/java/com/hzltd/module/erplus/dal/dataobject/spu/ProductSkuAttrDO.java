package com.hzltd.module.erplus.dal.dataobject.spu;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 商品 SKU 属性 DO
 * 
 * 用于存储 SKU 的扩展属性。
 * 
 * @author 翰展科技
 */
@TableName("erplus_product_sku_attrs")
@KeySequence("erplus_product_sku_attrs_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuAttrDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * SKU 编号
     */
    private Long skuId;

    /**
     * 属性 ID (键)
     */
    private String attrId;

    /**
     * 属性名
     */
    private String attrName;

    /**
     * 属性值 (JSON 或 文本)
     */
    private String attrValue;

    /**
     * 属性类路径
     */
    private String attrClass;

}
