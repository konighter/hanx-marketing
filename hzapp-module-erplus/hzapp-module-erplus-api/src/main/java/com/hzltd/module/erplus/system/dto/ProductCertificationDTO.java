package com.hzltd.module.erplus.system.dto;

import lombok.Data;

/**
 * 商品认证信息 DTO
 */
@Data
public class ProductCertificationDTO {
    /**
     * 认证名称
     */
    private String name;
    /**
     * 认证值
     */
    private String value;
}
