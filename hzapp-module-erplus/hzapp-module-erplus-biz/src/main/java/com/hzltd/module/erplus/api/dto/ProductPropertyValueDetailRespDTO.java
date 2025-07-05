package com.hzltd.module.erplus.api.dto;

import lombok.Data;

/**
 * 商品属性项的明细 Response DTO
 *
 * @author 翰展科技
 */
@Data
public class ProductPropertyValueDetailRespDTO {

    /**
     * 属性的编号
     */
    private Long propertyId;

    /**
     * 属性的名称
     */
    private String propertyName;

    /**
     * 属性值的编号
     */
    private Long valueId;

    /**
     * 属性值的名称
     */
    private String valueName;

}
