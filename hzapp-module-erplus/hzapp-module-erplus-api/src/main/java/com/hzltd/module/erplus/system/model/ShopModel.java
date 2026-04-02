package com.hzltd.module.erplus.system.model;

import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModelV0;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopModel {
    /**
     * ID
     */
    private Integer id;
    /**
     * 名称
     */
    private String name;

    /**
     * 卖家账号ID
     */
    private Long accountId;
    /**
     * 平台
     */
    private Integer platform;

    private String platformCode;
    /**
     * 区域
     */
    private String region;

    private String marketplace;

    private String countryCode;

    private String currency;

    private String language;

    private String sellerId;

    /**
     * 时区
     */
    private String timezone;
    /**
     * 授权信息
     */
    private AuthorizationModelV0 authInfo;
    /**
     * 授权刷新
     */
//    private String authRefreshInfo;
    /**
     * 授权开始时间
     */
    private LocalDateTime authStartTime;
    /**
     * 授权失效时间
     */
    private LocalDateTime authExpTime;


}
