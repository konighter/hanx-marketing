package com.hzltd.module.erplus.controller.admin.dashboard.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Schema(description = "管理后台 - ERP 业务大盘汇总数据 Response VO")
@Data
public class ErpDashboardSummaryRespVO {

    @Schema(description = "总订单量", example = "1250")
    private Long totalOrders;

    @Schema(description = "在售商品数", example = "450")
    private Long activeProducts;

    @Schema(description = "总库存水平", example = "8500")
    private Long totalInventory;

    @Schema(description = "全店动销率", example = "0.75")
    private BigDecimal sellThroughRate;

    @Schema(description = "动销店铺数", example = "12")
    private Integer activeStores;

}
