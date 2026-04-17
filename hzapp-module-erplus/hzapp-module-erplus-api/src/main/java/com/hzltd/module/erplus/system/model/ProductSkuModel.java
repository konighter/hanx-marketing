package com.hzltd.module.erplus.system.model;

import lombok.Data;
import java.util.Map;

@Data
public class ProductSkuModel {
    /**
     * SKU 编号
     */
    private Long id;

    /**
     * SKU 编码
     */
    private String code;

    /**
     * SKU 名字
     */
    private String name;

    /**
     * 图片地址
     */
    private String picUrl;

    /**
     * 商品属性
     */
    private Map<String, Object> attributes;
}
