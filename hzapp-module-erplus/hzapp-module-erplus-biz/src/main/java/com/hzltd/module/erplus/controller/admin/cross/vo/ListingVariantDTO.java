package com.hzltd.module.erplus.controller.admin.cross.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListingVariantDTO {

    private String sku;

    private String spec;

    private Integer price;

    private Integer stock;
}
