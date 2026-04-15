package com.hzltd.module.erplus.controller.admin.cross.vo;

import lombok.Data;

import java.util.List;

@Data
public class CrossProductSyncRequest extends CrossProductPageRequest {

    /**
     * 同步类型：all-全部, incremental-增量
     */
    private String syncType;

    /**
     * 跨境商品id列表
     */
    private List<String> productIds;

}
