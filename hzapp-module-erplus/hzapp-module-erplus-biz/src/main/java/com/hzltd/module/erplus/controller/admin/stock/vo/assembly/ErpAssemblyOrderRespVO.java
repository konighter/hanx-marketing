package com.hzltd.module.erplus.controller.admin.stock.vo.assembly;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - ERP 装配单详情 Response VO")
@Data
public class ErpAssemblyOrderRespVO {

    @Schema(description = "装配单编号", example = "1")
    private Long id;

    @Schema(description = "业务单号", example = "PROD-20260415-001")
    private String no;

    @Schema(description = "目标 SKU 编号", example = "1")
    private Long skuId;

    @Schema(description = "目标 SKU 名称", example = "商品A")
    private String skuName;

    @Schema(description = "仓库编号", example = "1")
    private Long warehouseId;

    @Schema(description = "仓库名称", example = "上海仓")
    private String warehouseName;

    @Schema(description = "计划生产数量", example = "100")
    private BigDecimal plannedQty;

    @Schema(description = "实际生产数量", example = "100")
    private BigDecimal actualQty;

    @Schema(description = "生产批次号", example = "PROD-20260415-001")
    private String batchNo;

    @Schema(description = "状态: 0-待启动, 1-装配中, 2-已完成, 3-已取消", example = "0")
    private Integer status;

    @Schema(description = "状态名称", example = "待启动")
    private String statusName;

    @Schema(description = "备注", example = "用于生产测试")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
