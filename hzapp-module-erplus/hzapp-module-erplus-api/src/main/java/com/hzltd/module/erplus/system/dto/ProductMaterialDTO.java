package com.hzltd.module.erplus.system.dto;

import lombok.Data;

/**
 * 商品材料信息 DTO
 */
@Data
public class ProductMaterialDTO {
    /**
     * 材料名称
     */
    private String name;
    /**
     * 含量百分比
     */
    private Double percentage;
    /**
     * 材料类型
     */
    private String type;
}
