package com.hzltd.module.erplus.adv.model;

import lombok.Data;
import java.time.LocalDate;

/**
 * 获取报表请求列表的请求参数
 */
@Data
public class AdsReportGetRequest {
    /** 报表起始日期 */
    private LocalDate startDate;
    /** 报表结束日期 */
    private LocalDate endDate;
}
