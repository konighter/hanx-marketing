package com.hzltd.module.erplus.dal.dataobject.productpub;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 跨境商品刊登状态 DO
 *
 * @author Antigravity
 */
@TableName("erplus_product_listing")
@KeySequence("erplus_product_listing_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductListingDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    
    @TableField("product_id")
    private Long productId;

    /**
     * 平台 ID
     */
    private Integer platformId;

    /**
     * 店铺 ID
     */
    private Integer shopId;

    /**
     * 市场 ID
     */
    private String marketId;

    /**
     * 卖家 SKU
     */
    private String sellerSku;
    
    /**
     * 刊登状态: 0-待发布, 10-待发布, 90-发布中, 91-发布失败, 99-发布成功
     */
    private Integer syncStatus;
    
    /**
     * 最新任务 ID
     */
    private Long latestTaskId;
    
    /**
     * 最近发布时间
     */
    private LocalDateTime publishTime;

}
