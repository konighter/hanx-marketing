package com.hzltd.module.erplus.model.common;

import lombok.Data;

@Data
public class AddressModel {
    /**
     * 姓名
     */
    private String name;
    /**
     * 手机号
     */
    private String phoneNumber;
    /**
     * 地址行1
     */
    private String addressLine1;
    /**
     * 地址行2
     */
    private String addressLine2;
    /**
     * 城市
     */
    private String city;
    /**
     * 国家代码
     */
    private String countryCode;
    /**
     * 邮政编码
     */
    private String postalCode;
    /**
     * 区域
     */
    private String stateOrProvinceCode;

}
