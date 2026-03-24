package com.hzltd.module.amz.api.adv.model.sp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Amazon SP 否定关键词 API 响应模型
 * 广告活动级：POST /sp/campaignNegativeKeywords
 * 广告组级：  POST /sp/negativeKeywords
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpNegativeKeyword {

    /** 关键词唯一标识 */
    private String keywordId;

    /** 所属广告活动 ID */
    private String campaignId;

    /** 所属广告组 ID（广告组级时有值；广告活动级时可能为 null） */
    private String adGroupId;

    /** 关键词文本 */
    private String keywordText;

    /** 匹配类型: NEGATIVE_EXACT / NEGATIVE_PHRASE */
    private String matchType;

    /** 状态 */
    private String state;
}
