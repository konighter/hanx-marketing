package com.hzltd.module.amz.api.adv.model.sp;

import com.hzltd.module.erplus.adv.metadata.vo.AdsAdVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Amazon SP ProductAd API 响应模型
 * 对应 application/vnd.spProductAd.v3+json
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpProductAd {

    /** 所属广告组 ID */
    private String adGroupId;

    /** 广告唯一标识 */
    private String adId;

    /** ASIN */
    private String asin;

    /** 所属广告活动 ID */
    private String campaignId;

    /** 自定义文本 (可选) */
    private String customText;

    /** 扩展数据 (投放状态、创建/更新时间等) */
    private AdsSpExtendedData extendedData;

    /** 全球广告 ID */
    private String globalAdId;

    /** 全球店铺设置 (可选) */
    private GlobalStoreSetting globalStoreSetting;

    /** SKU */
    private String sku;

    /** 广告状态: ENABLED, PAUSED, ARCHIVED */
    private String state;

    // ==================== 嵌套类 ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GlobalStoreSetting {
        /** 目录源国家代码 */
        private String catalogSourceCountryCode;
    }

    public AdsAdVO toVO() {
        return AdsAdVO.builder()
                .externalId(this.getAdId())
                .adGroupExternalId(this.getAdGroupId())
                .sku(this.getSku())
                .status(this.getState())
                .build();
    }
}
