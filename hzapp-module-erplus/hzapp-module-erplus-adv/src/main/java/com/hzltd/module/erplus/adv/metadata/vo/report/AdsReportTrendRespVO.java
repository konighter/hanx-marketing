package com.hzltd.module.erplus.adv.metadata.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - 广告报表趋势响应 VO")
@Data
public class AdsReportTrendRespVO {

    @Schema(description = "时间点列表 (横轴)")
    private List<String> timeList;

    @Schema(description = "各指标趋势数据")
    private Map<String, List<Object>> seriesData;
}
