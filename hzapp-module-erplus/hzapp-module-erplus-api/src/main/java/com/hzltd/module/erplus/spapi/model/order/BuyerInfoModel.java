package com.hzltd.module.erplus.spapi.model.order;

import lombok.Data;

@Data
public class BuyerInfoModel {
    /**
     * 买家姓名
     */
    private String  name;
    /**
     * 买家email
     */
    private String email;
    /**
     * 电话
     */
    private String phone;
    /**
     * 国家
     */
    private String country;
    /**
     * 城市
     */
    private String city;
    /**
     * 行政区
     */
    private String stateOrRegion;
    /**
     * 邮编
     */
    private String postalCode;
    /**
     * 详细地址
     */
    private String address;





}
