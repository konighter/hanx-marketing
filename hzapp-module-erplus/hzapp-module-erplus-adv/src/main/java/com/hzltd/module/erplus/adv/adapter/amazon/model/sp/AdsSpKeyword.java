package com.hzltd.module.erplus.adv.adapter.amazon.model.sp;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.metadata.vo.AdsKeywordVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Amazon SP Keyword API 响应模型
 * 对应 application/vnd.spKeyword.v3+json
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpKeyword {

    /** 所属广告组 ID */
    private String adGroupId;

    /** 出价 */
    private BigDecimal bid;

    /** 所属广告活动 ID */
    private String campaignId;

    /** 扩展数据 (投放状态、创建/更新时间等) */
    private AdsSpExtendedData extendedData;

    /** 全球关键词 ID */
    private String globalKeywordId;

    /** 关键词唯一标识 */
    private String keywordId;

    /** 关键词文本 */
    private String keywordText;

    /** 匹配类型: BROAD, PHRASE, EXACT */
    private String matchType;

    /** 本地语言关键词 (可选) */
    private String nativeLanguageKeyword;

    /** 本地语言语言代码 (可选) */
    private String nativeLanguageLocale;

    /** 关键词状态: ENABLED, PAUSED, ARCHIVED */
    private String state;

    public AdsKeywordVO toVO() {
        return AdsKeywordVO.builder()
                .externalId(this.getKeywordId())
                .adGroupExternalId(this.getAdGroupId())
                .keywordText(this.getKeywordText())
                .matchType(this.getMatchType())
                .bid(this.getBid())
                .status(this.getState())
                .isNegative(false)
                .extData(JsonUtils.toJsonString(this.getExtendedData()))
                .build();
    }
}
