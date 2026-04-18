package com.hzltd.module.erplus.controller.admin.stock.vo.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - ERP 库存账单新增/修改 Request VO")
@Data
public class ErpInventoryBillSaveReqVO {

    @Schema(description = "账单ID", example = "100")
    private Long id;

    @Schema(description = "单据编号", example = "BILL001")
    private String billCode;

    @Schema(description = "单据大类 (10:入, 20:出, 30:调, 40:平)", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "单据大类不能为空")
    private Integer type;

    @Schema(description = "来源类型 (WH, VENDOR, CUST, VIRTUAL)", example = "WH")
    private String fromType;

    @Schema(description = "来源ID", example = "WH_1")
    private String fromId;

    @Schema(description = "去向类型 (WH, VENDOR, CUST, VIRTUAL)", example = "WH")
    private String toType;

    @Schema(description = "去向ID", example = "WH_2")
    private String toId;

    @Schema(description = "业务前置单据类型", example = "PO")
    private String refType;

    @Schema(description = "关联单据号", example = "PO001")
    private String refCode;

    @Schema(description = "备注", example = "出入库备注")
    private String remark;

    @Schema(description = "账单项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "账单项列表不能为空")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {

        @Schema(description = "账单项编号", example = "1")
        private Long id;

        @Schema(description = "物料类型 (1: SKU, 2: Material)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        @NotNull(message = "物料类型不能为空")
        private Integer itemType;

        @Schema(description = "物料ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "101")
        @NotNull(message = "物料ID不能为空")
        private Long itemId;

        @Schema(description = "SKU", example = "SKU001")
        private String sellerSku;

        @Schema(description = "变动数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        @NotNull(message = "变动数量不能为空")
        private Integer qty;

    }

}
