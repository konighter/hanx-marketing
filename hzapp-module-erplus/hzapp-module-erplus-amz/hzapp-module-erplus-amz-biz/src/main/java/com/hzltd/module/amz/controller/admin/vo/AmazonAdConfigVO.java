package com.hzltd.module.amz.controller.admin.vo;

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

    public static AmazonAdConfigVO fromSpProductAd(AdsSpProductAd spProductAd) {
        if (spProductAd == null) {
            return null;
        }
        return AmazonAdConfigVO.builder()
                .globalAdId(spProductAd.getGlobalAdId())
                .customText(spProductAd.getCustomText())
                .extendedData(spProductAd.getExtendedData())
                .globalStoreSetting(spProductAd.getGlobalStoreSetting())
                .build();
    }
}
