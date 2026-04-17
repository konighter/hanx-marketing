package com.hzltd.module.erplus.dal.dataobject.material;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.module.erplus.dal.dataobject.spu.ProductSpuDO;
import lombok.*;

import java.math.BigDecimal;

/**
 * ERP 商品耗材 BOM DO
 *
 * @author 翰展科技
 */
@TableName("erplus_product_material")
@KeySequence("erplus_product_material_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErpProductMaterialDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 商品 SKU 编号
     *
     * 关联 {@link ProductSkuDO#getId()}
     */
    private Long skuId;
    /**
     * 耗材编号
     *
     * 关联 {@link ErpMaterialDO#getId()}
     */
    private Long materialId;
    /**
     * 单个成品的标称用量
     */
    private BigDecimal usageQuantity;

}
