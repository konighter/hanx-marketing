package com.hzltd.module.erplus.controller.admin.shop.vo;

import lombok.Data;

import java.util.List;


/**
 * 店铺级联信息响应VO
 * 平台 -> 店铺-> 子店铺
 */
@Data
public class CascaderShopRespVO {

    private Integer id;

    private String name;

    private List<CascaderShopRespVO> children;


}
