package com.hzltd.module.erplus.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Schema(description = "管理后台 - ERP 业务大盘图表数据 Response VO")
@Data
public class ErpDashboardChartRespVO {

    @Schema(description = "图表名称", example = "多店铺 GMV 趋势")
    private String name;

    @Schema(description = "X轴标签/类别", example = "['2024-05-01', '2024-05-02']")
    private List<String> labels;

    @Schema(description = "数据集列表")
    private List<Dataset> datasets;

    @Data
    public static class Dataset {
        @Schema(description = "数据序列名称", example = "TikTok Shop")
        private String label;
        
        @Schema(description = "数值列表", example = "[120, 150, 180]")
        private List<Object> data;

        @Schema(description = "额外属性 (颜色、类型等)", example = "{'color': '#ff4d4f'}")
        private Map<String, Object> extra;
    }
}
