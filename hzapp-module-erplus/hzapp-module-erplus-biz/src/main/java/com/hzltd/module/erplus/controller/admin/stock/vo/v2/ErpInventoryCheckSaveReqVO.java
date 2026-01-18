package com.hzltd.module.erplus.controller.admin.stock.vo.v2;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - ERP 库存盘点新增/修改 Request VO")
@Data
public class ErpInventoryCheckSaveReqVO {

    @Schema(description = "盘点单编号", example = "100")
    private Long id;

    @Schema(description = "盘点单号", example = "CHECK001")
    private String checkCode;

    @Schema(description = "盘点仓库 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "盘点仓库不能为空")
    private Long warehouseId;

    @Schema(description = "备注", example = "盘点备注")
    private String remark;

    @Schema(description = "盘点执行人 ID", example = "1")
    private Long checkUserId;

    @Schema(description = "盘点项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "盘点项列表不能为空")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {

        @Schema(description = "盘点项编号", example = "1")
        private Long id;

        @Schema(description = "SKU", requiredMode = Schema.RequiredMode.REQUIRED, example = "SKU001")
        @NotNull(message = "SKU不能为空")
        private String sellerSku;

        @Schema(description = "账面数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
        @NotNull(message = "账面数量不能为空")
        private Integer bookQty;

        @Schema(description = "盘点数量", example = "105")
        private Integer checkQty;

        @Schema(description = "备注", example = "备注")
        private String remark;

    }

}
