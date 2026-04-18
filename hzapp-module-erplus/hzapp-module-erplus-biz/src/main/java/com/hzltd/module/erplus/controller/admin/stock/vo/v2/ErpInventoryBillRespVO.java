package com.hzltd.module.erplus.controller.admin.stock.vo.v2;

import cn.idev.excel.annotation.ExcelProperty;
import com.hzltd.framework.excel.core.annotations.DictFormat;
import com.hzltd.framework.excel.core.convert.DictConvert;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - ERP 库存账单 Response VO")
@Data
public class ErpInventoryBillRespVO {

    @Schema(description = "账单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("账单ID")
    private Long id;

    @Schema(description = "单据编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "BILL001")
    @ExcelProperty("单据编号")
    private String billCode;

    @Schema(description = "单据大类 (10:入, 20:出, 30:调, 40:平)", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @ExcelProperty(value = "单据大类", converter = DictConvert.class)
    @DictFormat("erp_inventory_bill_type")
    private Integer type;

    @Schema(description = "来源类型", example = "WH")
    @ExcelProperty("来源类型")
    private String fromType;

    @Schema(description = "来源ID", example = "WH_1")
    @ExcelProperty("来源ID")
    private String fromId;

    @Schema(description = "去向类型", example = "WH")
    @ExcelProperty("去向类型")
    private String toType;

    @Schema(description = "去向ID", example = "WH_2")
    @ExcelProperty("去向ID")
    private String toId;

    @Schema(description = "业务前置单据类型", example = "PO")
    @ExcelProperty("业务类型")
    private String refType;

    @Schema(description = "关联单据号", example = "PO001")
    @ExcelProperty("关联单据号")
    private String refCode;

    @Schema(description = "操作人ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty("操作人ID")
    private Long operatorId;

    @Schema(description = "状态 (10:完成, 90:作废)", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @ExcelProperty(value = "状态", converter = DictConvert.class)
    @DictFormat("erp_inventory_bill_status")
    private Integer status;

    @Schema(description = "备注", example = "备注信息")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "账单项列表")
    private List<Item> items;

    @Data
    public static class Item {

        @Schema(description = "明细ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
        private Long id;

        @Schema(description = "物料类型 (1: SKU, 2: Material)", example = "1")
        private Integer itemType;

        @Schema(description = "物料ID", example = "101")
        private Long itemId;

        @Schema(description = "SKU", example = "SKU001")
        private String sellerSku;

        @Schema(description = "变动数量", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
        private Integer qty;

        @Schema(description = "期末存", example = "100")
        private Integer snapshotQty;

    }

}
