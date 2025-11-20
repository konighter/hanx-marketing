package com.hzltd.module.erplus.model.category;

import lombok.Data;

@Data
public class BrandModel {

    /**
     * 品牌ID
     */
    private String id;

    /**
     * 品牌名
     */
    private String name;

     /**
     * 品牌编码
     */
    private String code;

    /**
     * 品牌状态
     */
    private Integer status;

    /**
     * 授权状态
     */
    private Integer authorizedStatus;


}
