package com.hzltd.module.erplus.controller.admin.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class CrossProductSyncRequest extends CrossProductPageRequest {

    /**
     * 跨境商品id列表
     */
    private List<Long> productIds;

}
