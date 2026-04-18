package com.hzltd.module.erplus.controller.admin.stock.v2;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.*;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryBillItemDO;
import com.hzltd.module.erplus.service.stock.v2.ErpInventoryBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - ERP 库存账单 (V2)")
@RestController
@RequestMapping("/erplus/inventory-bill")
@Validated
public class ErpInventoryBillController {

    @Resource
    private ErpInventoryBillService inventoryBillService;

    @PostMapping("/create")
    @Operation(summary = "创建库存账单")
    @PreAuthorize("@ss.hasPermission('erp:inventory-bill:create')")
    public CommonResult<Long> createInventoryBill(@Valid @RequestBody ErpInventoryBillSaveReqVO createReqVO) {
        return success(inventoryBillService.createInventoryBill(createReqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "获得库存账单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:inventory-bill:query')")
    public CommonResult<ErpInventoryBillRespVO> getInventoryBill(@RequestParam("id") Long id) {
        ErpInventoryBillDO bill = inventoryBillService.getInventoryBill(id);
        if (bill == null) {
            return success(null);
        }
        List<ErpInventoryBillItemDO> items = inventoryBillService.getInventoryBillItemListByBillId(id);
        return success(BeanUtils.toBean(bill, ErpInventoryBillRespVO.class)
                .setItems(BeanUtils.toBean(items, ErpInventoryBillRespVO.Item.class)));
    }

    @GetMapping("/page")
    @Operation(summary = "获得库存账单分页")
    @PreAuthorize("@ss.hasPermission('erp:inventory-bill:query')")
    public CommonResult<PageResult<ErpInventoryBillRespVO>> getInventoryBillPage(
            @Valid ErpInventoryBillPageReqVO pageReqVO) {
        PageResult<ErpInventoryBillDO> pageResult = inventoryBillService.getInventoryBillPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ErpInventoryBillRespVO.class));
    }

    @GetMapping("/item-page")
    @Operation(summary = "获得库存账单明细分页")
    @PreAuthorize("@ss.hasPermission('erp:inventory-bill:query')")
    public CommonResult<PageResult<ErpInventoryBillItemRespVO>> getInventoryBillItemPage(
            @Valid ErpInventoryBillItemPageReqVO pageReqVO) {
        PageResult<ErpInventoryBillItemDO> pageResult = inventoryBillService.getInventoryBillItemPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ErpInventoryBillItemRespVO.class));
    }

    @PutMapping("/receive")
    @Operation(summary = "确认收货 (仅调拨单)")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:inventory-bill:update')")
    public CommonResult<Boolean> receiveInventoryBill(@RequestParam("id") Long id) {
        inventoryBillService.receiveInventoryBill(id);
        return success(true);
    }

}
