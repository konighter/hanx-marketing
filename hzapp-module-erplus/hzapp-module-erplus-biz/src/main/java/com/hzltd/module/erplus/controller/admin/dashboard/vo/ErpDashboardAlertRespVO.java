package com.hzltd.module.erplus.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - ERP 业务大盘预警数据 Response VO")
@Data
public class ErpDashboardAlertRespVO {

    @Schema(description = "预警类型 (SALES_DROP, REFUND_HIGH, STOCK_LOW, ACOS_HIGH)", example = "SALES_DROP")
    private String type;

    @Schema(description = "预警标题", example = "销量大幅下滑")
    private String title;

    @Schema(description = "当前数值", example = "85")
    private String value;

    @Schema(description = "环比变化", example = "-0.25")
    private BigDecimal delta;

    @Schema(description = "提示信息", example = "3个店铺销量跌幅超过20%")
    private String message;

}
