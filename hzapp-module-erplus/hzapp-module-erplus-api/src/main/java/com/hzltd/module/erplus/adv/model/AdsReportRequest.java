package com.hzltd.module.erplus.adv.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * 广告报表同步请求 DTO
 * 
 * 平台无关，由 adv 模块下传给平台模块
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsReportRequest {

    /** 店铺ID */
    private Long shopId;
    
    /** 广告平台 */
    private String platform;
    
    /** 报表起始日期 */
    private LocalDate startDate;
    
    /** 报表结束日期 */
    private LocalDate endDate;
    
    /** 
     * 平台特定上下文 
     * Amazon 示例: {"profileId": "...", "reportType": "spAds"}
     */
    private Map<String, Object> context;

}
