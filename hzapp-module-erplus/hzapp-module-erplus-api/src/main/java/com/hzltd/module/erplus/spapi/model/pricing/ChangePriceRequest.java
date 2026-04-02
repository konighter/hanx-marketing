package com.hzltd.module.erplus.spapi.model.pricing;

import com.hzltd.module.erplus.spapi.model.common.PriceModel;
import lombok.Data;

import java.util.List;

@Data
public class ChangePriceRequest {
    /**
     * 商品平台编码
     */
    private String productCode;

    /**
     * 商品分类
     */
    private String category;

    /**
     * 商品SKU
     */
    private String sellerSku;
    /**
     * 商品 listing 价格
     */
    private PriceModel listingPrice;
    /**
     * 商品 sale 价格, 多个价格按时间生效, 校验不重叠
     */
    private List<PriceModel> salePrice;

    /**
     * 是否预览模式
     */
    private boolean isPreview = true;
}
