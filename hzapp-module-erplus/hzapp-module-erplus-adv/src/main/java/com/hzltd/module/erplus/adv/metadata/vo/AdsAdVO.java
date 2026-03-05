package com.hzltd.module.erplus.adv.metadata.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 广告素材/广告实体 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAdVO {

    private String externalId;
    private String adGroupExternalId;
    private String name;
    private String adFormat;
    private String status;
    private String headline;
    private String description;
    private String landingPageUrl;
    private String callToAction;
    private String reviewStatus;
    private String asin;
    private String sku;
    private Object extData; // JSON or Object

}
