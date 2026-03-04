package com.hzltd.module.erplus.adv.adapter.amazon.v1.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Amazon Ads Reporting API V3.0 Report Request
 * See: https://advertising.amazon.com/API/docs/en-us/reporting/v3/reporting-api-resource#post-reportingreports
 *
 * Amazon API v3 要求 configuration 作为嵌套对象:
 * {
 *   "name": "...",
 *   "startDate": "...",
 *   "endDate": "...",
 *   "configuration": {
 *     "adProduct": "SPONSORED_PRODUCTS",
 *     "reportTypeId": "spCampaigns",
 *     "columns": [...],
 *     "timeUnit": "DAILY",
 *     "format": "GZIP_JSON"
 *   }
 * }
 */
@Data
@Builder
public class AmzReportRequest {
    /** 报告名称 */
    private String name;
    /** 开始日期 yyyy-MM-dd */
    private String startDate;
    /** 结束日期 yyyy-MM-dd */
    private String endDate;
    /** 配置对象（包含 adProduct, reportTypeId, columns, timeUnit, format） */
    private Map<String, Object> configuration;
}
