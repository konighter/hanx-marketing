package com.hzltd.module.amz.adv.controller.admin.vo;

import com.hzltd.module.amz.api.adv.model.sp.AdsSpProductAd;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 亚马逊广告实体配置 VO")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmazonAdConfigVO {

    @Schema(description = "站点的 Profile ID")
    private String profileId;

    @Schema(description = "扩展数据")
    private Object extendedData;

    @Schema(description = "全球广告 ID")
    private String globalAdId;

    @Schema(description = "自定义文本")
    private String customText;

    @Schema(description = "全球店铺设置")
    private Object globalStoreSetting;

    @Schema(description = "ASIN")
    private String asin;

    @Schema(description = "SKU")
    private String sku;

    public static AmazonAdConfigVO fromSpProductAd(AdsSpProductAd spProductAd) {
        if (spProductAd == null) {
            return null;
        }
        return AmazonAdConfigVO.builder()
                .globalAdId(spProductAd.getGlobalAdId())
                .customText(spProductAd.getCustomText())
                .extendedData(spProductAd.getExtendedData())
                .globalStoreSetting(spProductAd.getGlobalStoreSetting())
                .asin(spProductAd.getAsin())
                .sku(spProductAd.getSku())
                .build();
    }

    public java.util.Map<String, Object> toAttributes() {
        java.util.Map<String, Object> attrs = new java.util.HashMap<>();
        if (profileId != null) attrs.put("amz.profile_id", profileId);
        if (extendedData != null) attrs.put("amz.extended_data", extendedData);
        if (globalAdId != null) attrs.put("amz.global_ad_id", globalAdId);
        if (customText != null) attrs.put("amz.custom_text", customText);
        if (globalStoreSetting != null) attrs.put("amz.global_store_setting", globalStoreSetting);
        if (asin != null) attrs.put("amz.asin", asin);
        if (sku != null) attrs.put("amz.sku", sku);
        return attrs;
    }

    public static AmazonAdConfigVO fromAttributes(java.util.Map<String, Object> attributes) {
        if (attributes == null) return null;
        return AmazonAdConfigVO.builder()
                .profileId((String) attributes.get("amz.profile_id"))
                .extendedData(attributes.get("amz.extended_data"))
                .globalAdId((String) attributes.get("amz.global_ad_id"))
                .customText((String) attributes.get("amz.custom_text"))
                .globalStoreSetting(attributes.get("amz.global_store_setting"))
                .asin((String) attributes.get("amz.asin"))
                .sku((String) attributes.get("amz.sku"))
                .build();
    }
}
