package com.hzltd.module.erplus.controller.admin.stock.v2;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryCheckPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryCheckRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.v2.ErpInventoryCheckSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryCheckDO;
import com.hzltd.module.erplus.dal.dataobject.stock.v2.ErpInventoryCheckItemDO;
import com.hzltd.module.erplus.service.stock.v2.ErpInventoryCheckService;
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

@Tag(name = "管理后台 - ERP 库存盘点 (V2)")
@RestController
@RequestMapping("/erplus/inventory-check")
@Validated
public class ErpInventoryCheckController {

    @Resource
    private ErpInventoryCheckService inventoryCheckService;

    @PostMapping("/create")
    @Operation(summary = "创建库存盘点单")
    @PreAuthorize("@ss.hasPermission('erp:inventory-check:create')")
    public CommonResult<Long> createInventoryCheck(@Valid @RequestBody ErpInventoryCheckSaveReqVO createReqVO) {
        return success(inventoryCheckService.createInventoryCheck(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新库存盘点结果")
    @PreAuthorize("@ss.hasPermission('erp:inventory-check:update')")
    public CommonResult<Boolean> updateInventoryCheck(@Valid @RequestBody ErpInventoryCheckSaveReqVO updateReqVO) {
        inventoryCheckService.updateInventoryCheck(updateReqVO);
        return success(true);
    }

    @PutMapping("/approve")
    @Operation(summary = "审核库存盘点单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:inventory-check:approve')")
    public CommonResult<Boolean> approveInventoryCheck(@RequestParam("id") Long id) {
        inventoryCheckService.approveInventoryCheck(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得库存盘点单")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erp:inventory-check:query')")
    public CommonResult<ErpInventoryCheckRespVO> getInventoryCheck(@RequestParam("id") Long id) {
        ErpInventoryCheckDO check = inventoryCheckService.getInventoryCheck(id);
        if (check == null) {
            return success(null);
        }
        List<ErpInventoryCheckItemDO> items = inventoryCheckService.getInventoryCheckItemListByCheckId(id);
        return success(BeanUtils.toBean(check, ErpInventoryCheckRespVO.class)
                .setItems(BeanUtils.toBean(items, ErpInventoryCheckRespVO.Item.class)));
    }

    @GetMapping("/page")
    @Operation(summary = "获得库存盘点单分页")
    @PreAuthorize("@ss.hasPermission('erp:inventory-check:query')")
    public CommonResult<PageResult<ErpInventoryCheckRespVO>> getInventoryCheckPage(
            @Valid ErpInventoryCheckPageReqVO pageReqVO) {
        PageResult<ErpInventoryCheckDO> pageResult = inventoryCheckService.getInventoryCheckPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ErpInventoryCheckRespVO.class));
    }

}
