package com.hzltd.module.erplus.system.dto;

import lombok.Data;
import java.util.List;

/**
 * 商品图片信息 DTO
 */
@Data
public class ProductMediaDTO {

    /**
     * 商品主图
     */
    private String mainImage;

    /**
     * 附加图片列表
     */
    private List<String> additionalImages;

}
