package com.hzltd.module.erplus.controller.admin.stock;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.MapUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.spu.vo.ProductSpuRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyItemRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderPageReqVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderRespVO;
import com.hzltd.module.erplus.controller.admin.stock.vo.assembly.ErpAssemblyOrderSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyItemDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpAssemblyOrderDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.service.material.ErpMaterialService;
import com.hzltd.module.erplus.service.spu.ProductSkuService;
import com.hzltd.module.erplus.service.stock.ErpAssemblyOrderService;
import com.hzltd.module.erplus.service.stock.ErpWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.hzltd.framework.apilog.core.enums.OperateTypeEnum.*;
import static com.hzltd.framework.common.pojo.CommonResult.success;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertList;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertSet;

@Tag(name = "管理后台 - ERP 装配单")
@RestController
@RequestMapping("/erplus/assembly-order")
@Validated
public class ErpAssemblyOrderController {

    @Resource
    private ErpAssemblyOrderService assemblyOrderService;
    @Resource
    private ErpWarehouseService warehouseService;
    @Resource
    private ErpMaterialService materialService;
    @Resource
    private ProductSkuService productSkuService;

    @PostMapping("/create")
    @Operation(summary = "创建装配单")
    @PreAuthorize("@ss.hasPermission('erp:assembly:create')")
    @ApiAccessLog(operateType = CREATE)
    public CommonResult<Long> createAssemblyOrder(@Valid @RequestBody ErpAssemblyOrderSaveReqVO reqVO) {
        return success(assemblyOrderService.createAssemblyOrder(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新装配单")
    @PreAuthorize("@ss.hasPermission('erp:assembly:update')")
    @ApiAccessLog(operateType = UPDATE)
    public CommonResult<Boolean> updateAssemblyOrder(@Valid @RequestBody ErpAssemblyOrderSaveReqVO reqVO) {
        assemblyOrderService.updateAssemblyOrder(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除装配单")
    @Parameter(name = "id", description = "编号", example = "1")
    @PreAuthorize("@ss.hasPermission('erp:assembly:delete')")
    @ApiAccessLog(operateType = DELETE)
    public CommonResult<Boolean> deleteAssemblyOrder(@RequestParam("id") Long id) {
        assemblyOrderService.deleteAssemblyOrder(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得装配单")
    @Parameter(name = "id", description = "编号", example = "1")
    public CommonResult<ErpAssemblyOrderRespVO> getAssemblyOrder(@RequestParam("id") Long id) {
        ErpAssemblyOrderDO order = assemblyOrderService.getAssemblyOrder(id);
        ErpAssemblyOrderRespVO respVO = BeanUtils.toBean(order, ErpAssemblyOrderRespVO.class);
        
        if (respVO != null && order != null) {
            MapUtils.findAndThen(warehouseService.getWarehouseMap(convertSet(List.of(order), ErpAssemblyOrderDO::getWarehouseId)),
                    order.getWarehouseId(), warehouse -> respVO.setWarehouseName(warehouse.getName()));
        }
        
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得装配单分页")
    @PreAuthorize("@ss.hasPermission('erp:assembly:query')")
    public CommonResult<PageResult<ErpAssemblyOrderRespVO>> getAssemblyOrderPage(@Valid ErpAssemblyOrderPageReqVO pageReqVO) {
        PageResult<ErpAssemblyOrderDO> pageResult = assemblyOrderService.getAssemblyOrderPage(pageReqVO);
        
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        
        Map<Long, ErpWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                convertSet(pageResult.getList(), ErpAssemblyOrderDO::getWarehouseId));
        
        return success(BeanUtils.toBean(pageResult, ErpAssemblyOrderRespVO.class, order -> {
            MapUtils.findAndThen(warehouseMap, order.getWarehouseId(), 
                    warehouse -> order.setWarehouseName(warehouse.getName()));
            if (order.getStatus() != null) {
                order.setStatusName(getStatusName(order.getStatus()));
            }
        }));
    }

    @PutMapping("/start")
    @Operation(summary = "启动装配单")
    @Parameter(name = "id", description = "编号", example = "1")
    @PreAuthorize("@ss.hasPermission('erp:assembly:update')")
    public CommonResult<Boolean> startAssemblyOrder(@RequestParam("id") Long id) {
        assemblyOrderService.startAssemblyOrder(id);
        return success(true);
    }

    @PutMapping("/complete")
    @Operation(summary = "完成装配单")
    @Parameter(name = "id", description = "编号", example = "1")
    @PreAuthorize("@ss.hasPermission('erp:assembly:update')")
    public CommonResult<Boolean> completeAssemblyOrder(@RequestParam("id") Long id) {
        assemblyOrderService.completeAssemblyOrder(id);
        return success(true);
    }

    @PutMapping("/cancel")
    @Operation(summary = "取消装配单")
    @Parameter(name = "id", description = "编号", example = "1")
    @PreAuthorize("@ss.hasPermission('erp:assembly:update')")
    public CommonResult<Boolean> cancelAssemblyOrder(@RequestParam("id") Long id) {
        assemblyOrderService.cancelAssemblyOrder(id);
        return success(true);
    }

    @GetMapping("/item-list")
    @Operation(summary = "获得装配单耗材明细")
    @Parameter(name = "orderId", description = "装配单编号", example = "1")
    public CommonResult<List<ErpAssemblyItemRespVO>> getAssemblyItemList(@RequestParam("orderId") Long orderId) {
        List<ErpAssemblyItemDO> items = assemblyOrderService.getAssemblyItemList(orderId);
        
        if (CollUtil.isEmpty(items)) {
            return success(List.of());
        }
        
        List<ErpMaterialDO> materials = materialService.getMaterialList(
                convertList(items, ErpAssemblyItemDO::getMaterialId));
        Map<Long, ErpMaterialDO> materialMap = CollUtil.isEmpty(materials) ? Map.of() :
                materials.stream().collect(java.util.stream.Collectors.toMap(ErpMaterialDO::getId, m -> m));
        
        return success(BeanUtils.toBean(items, ErpAssemblyItemRespVO.class, item -> {
            ErpMaterialDO material = materialMap.get(item.getMaterialId());
            if (material != null) {
                item.setMaterialName(material.getName());
                item.setMaterialCode(material.getCode());
//                item.setMaterialUnit(material.getUnit());
            }
            item.setIsShortfall(item.getShortfallQty() != null && item.getShortfallQty().compareTo(java.math.BigDecimal.ZERO) > 0);
        }));
    }

    private String getStatusName(Integer status) {
        if (status == null) return "";
        return switch (status) {
            case 0 -> "待启动";
            case 1 -> "装配中";
            case 2 -> "已完成";
            case 3 -> "已取消";
            default -> "";
        };
    }

}
