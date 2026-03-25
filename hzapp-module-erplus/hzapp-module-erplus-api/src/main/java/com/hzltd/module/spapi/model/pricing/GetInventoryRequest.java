package com.hzltd.module.spapi.model.pricing;

import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;

import java.util.List;

@Data
public class GetInventoryRequest extends PageParam {
    /**
     * 分页token
     */
    private String nextToken;

    /**
     * 商品sku列表
     */
    private List<String> sellerSkus;

    /**
     * 商品code列表
     */
    private List<String> productCodes;

}
