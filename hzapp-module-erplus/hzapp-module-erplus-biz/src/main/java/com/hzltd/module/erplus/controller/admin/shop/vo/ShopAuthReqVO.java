package com.hzltd.module.erplus.controller.admin.shop.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ShopAuthReqVO {

    private Integer platformId;
    @JsonProperty("id")
    private Integer shopId;
    private boolean selfAuth;
    private String appKey;
    private String appSecret;
    private String refreshToken;
    /**
     * 卖家ID (非必填, 自授权时填入)
     */
    private String sellerId;
}
