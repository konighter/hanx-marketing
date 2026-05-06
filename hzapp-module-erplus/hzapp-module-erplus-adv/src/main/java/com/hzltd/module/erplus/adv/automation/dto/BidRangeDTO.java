package com.hzltd.module.erplus.adv.automation.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 出价范围 DTO
 * 用于在利润模型约束下计算合理的出价空间
 *
 * @author antigravity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BidRangeDTO {

    /**
     * 最低建议出价 (通常为保命价)
     */
    private Double minBid;

    /**
     * 最高允许出价 (基于利润模型的上限)
     */
    private Double maxBid;

    /**
     * 目标 CPC (在目标 ACOS 下的理想出价)
     */
    private Double targetCpc;

    /**
     * 预估利润率 (在此出价下的剩余利润)
     */
    private Double estimatedMargin;

}
