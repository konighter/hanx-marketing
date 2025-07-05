package com.hzltd.module.erpls.api.model.common;

import lombok.Data;

@Data
public class BrandModel {

    /**
     * 品牌ID, 通常是本地ID
     */
    private String id;

    /**
     * 品牌名
     */
    private String name;

    /**
     * 品牌图
     */
    private Image image;

    /**
     * 品牌认证文件
     */
    private CertificationModel certification;

}
