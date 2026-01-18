package com.hzltd.module.erplus.controller.admin.stock.vo.v2;

import cn.idev.excel.annotation.ExcelProperty;
import com.hzltd.framework.excel.core.annotations.DictFormat;
import com.hzltd.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - ERP 库存盘点 Response VO")
@Data
public class ErpInventoryCheckRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("盘点ID")
    private Long id;

    @Schema(description = "盘点单号", requiredMode = Schema.RequiredMode.REQUIRED, example = "CHECK001")
    @ExcelProperty("盘点单号")
    private String checkCode;

    @Schema(description = "盘点仓库 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("仓库ID")
    private Long warehouseId;

    @Schema(description = "状态 (1: 进行中, 10: 待审核, 20: 已过账, 90: 已取消)", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("erp_inventory_check_status")
    private Integer status;

    @Schema(description = "创建者 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("创建者ID")
    private Long operatorId;

    @Schema(description = "盘点执行人 ID", example = "1")
    @ExcelProperty("执行人ID")
    private Long checkUserId;

    @Schema(description = "备注", example = "备注")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "盘点项列表")
    private List<Item> items;

    @Data
    public static class Item {

        @Schema(description = "明细ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "SKU", requiredMode = Schema.RequiredMode.REQUIRED, example = "SKU001")
        private String sellerSku;

        @Schema(description = "账面数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
        private Integer bookQty;

        @Schema(description = "盘点数量", example = "105")
        private Integer checkQty;

        @Schema(description = "差异数量", example = "5")
        private Integer diffQty;

        @Schema(description = "关联的库存流水 ID", example = "1000")
        private Long adjustBillId;

        @Schema(description = "备注", example = "备注")
        private String remark;

    }

}
