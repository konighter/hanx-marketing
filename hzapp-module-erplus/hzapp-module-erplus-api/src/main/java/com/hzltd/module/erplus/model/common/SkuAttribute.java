package com.hzltd.module.erplus.model.common;


import java.util.List;

/**
 *
 *        {
 *           "id": "100089",
 *           "value_id": "1729592969712207000",
 *           "value_name": "Red",
 *           "sku_img": {
 *             "uri": "tos-maliva-i-o3syd03w52-us/c668cdf70b7f483c94dbe"
 *           },
 *           "name": "Color",
 *           "supplementary_sku_images": [
 *             {
 *               "uri": "tos-maliva-i-o3syd03w52-us/c668cdf70b7f483c94dbe"
 *             }
 *           ]
 *         }
 *
 *
 *
 */


public class SkuAttribute {

    private String id;

    private String name;

    private String valueId;

    private String valueName;

    private Image skuImg;

    private List<Image> extSkuImgs;

}
