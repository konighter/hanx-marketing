package com.hzltd.module.erplus.controller.admin.stock.vo.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - ERP 库存账单明细 Response VO")
@Data
public class ErpInventoryBillItemRespVO {

    @Schema(description = "明细ID", example = "1")
    private Long id;

    @Schema(description = "账单ID", example = "10")
    private Long billId;

    @Schema(description = "SKU", example = "SKU001")
    private String sellerSku;

    @Schema(description = "变动数量", example = "10")
    private Integer qty;

    @Schema(description = "期末存", example = "100")
    private Integer snapshotQty;

    // 以下是主表信息
    @Schema(description = "单据编号", example = "BILL001")
    private String billCode;

    @Schema(description = "单据大类", example = "10")
    private Integer type;

    @Schema(description = "关联单据类型", example = "SO")
    private String refType;

    @Schema(description = "关联单据号", example = "SO001")
    private String refCode;

    @Schema(description = "来源ID", example = "1")
    private String fromId;

    @Schema(description = "去向ID", example = "2")
    private String toId;

    @Schema(description = "备注", example = "备注信息")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
