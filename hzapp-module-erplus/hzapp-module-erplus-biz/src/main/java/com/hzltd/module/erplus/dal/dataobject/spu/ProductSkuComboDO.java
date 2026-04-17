package com.hzltd.module.erplus.dal.dataobject.spu;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 商品 SKU 组合关联 DO
 *
 * @author 翰展科技
 */
@TableName(value = "erplus_product_sku_combo", autoResultMap = true)
@KeySequence("product_sku_combo_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSkuComboDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 父 SKU 编号 (组合 SKU)
     * 关联 {@link ProductSkuDO#getId()}
     */
    private Long parentSkuId;
    /**
     * 子 SKU 编号 (成分 SKU)
     * 关联 {@link ProductSkuDO#getId()}
     */
    private Long childSkuId;
    /**
     * 组成数量
     */
    private Integer quantity;

}
