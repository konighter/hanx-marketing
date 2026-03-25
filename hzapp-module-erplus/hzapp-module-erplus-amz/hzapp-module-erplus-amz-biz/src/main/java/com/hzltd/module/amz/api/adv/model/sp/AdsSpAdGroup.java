package com.hzltd.module.amz.api.adv.model.sp;

import com.hzltd.module.erplus.adv.metadata.vo.AdsAdGroupVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Amazon SP AdGroup API 响应模型
 * 对应 application/vnd.spAdGroup.v3+json
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsSpAdGroup {

    /** 广告组唯一标识 */
    private String adGroupId;

    /** 所属广告活动 ID */
    private String campaignId;

    /** 默认出价 */
    private BigDecimal defaultBid;

    /** 全球广告组 ID */
    private String globalAdGroupId;

    /** 广告组名称 */
    private String name;

    /** 广告组状态: ENABLED, PAUSED, ARCHIVED */
    private String state;

    /** 扩展数据 (投放状态、创建/更新时间等) */
    private AdsSpExtendedData extendedData;

    public AdsAdGroupVO toVO() {
        return AdsAdGroupVO.builder()
                .externalId(this.getAdGroupId())
                .campaignExternalId(this.getCampaignId())
                .name(this.getName())
                .status(this.getState())
                .defaultBid(this.getDefaultBid())
                .build();
    }


}
