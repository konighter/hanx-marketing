package com.hzltd.module.amz.api.adv.model.sp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Amazon SP 通用扩展数据模型
 * Campaign / AdGroup / Ad / Keyword 等实体共用
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpExtendedData {

    /** 创建时间 */
    private String creationDateTime;

    /** 最后更新时间 */
    private String lastUpdateDateTime;

    /** 投放状态: ADVERTISER_ARCHIVED, CAMPAIGN_OUT_OF_BUDGET, AD_GROUP_PAUSED 等 */
    private String servingStatus;

    /** 投放状态详情列表 */
    private List<ServingStatusDetail> servingStatusDetails;

    // ==================== 嵌套类 ====================

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServingStatusDetail {

        /** 帮助链接 */
        private String helpUrl;

        /** 状态描述信息 */
        private String message;

        /** 状态详情名称: ADVERTISER_ARCHIVED_DETAIL 等 */
        private String name;
    }
}
