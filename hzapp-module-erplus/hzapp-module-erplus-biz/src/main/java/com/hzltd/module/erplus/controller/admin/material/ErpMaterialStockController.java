package com.hzltd.module.erplus.controller.admin.material;

import cn.hutool.core.collection.CollUtil;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.collection.MapUtils;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.material.vo.stock.ErpMaterialStockRespVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialStockDO;
import com.hzltd.module.erplus.dal.dataobject.stock.ErpWarehouseDO;
import com.hzltd.module.erplus.service.material.ErpMaterialService;
import com.hzltd.module.erplus.service.material.ErpMaterialStockService;
import com.hzltd.module.erplus.service.stock.ErpWarehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.hzltd.framework.common.pojo.CommonResult.success;
import static com.hzltd.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - ERP 耗材库存")
@RestController
@RequestMapping("/erplus/material-stock")
@Validated
public class ErpMaterialStockController {

    @Resource
    private ErpMaterialStockService materialStockService;
    @Resource
    private ErpMaterialService materialService;
    @Resource
    private ErpWarehouseService warehouseService;

    @GetMapping("/page")
    @Operation(summary = "获得耗材库存分页")
    public CommonResult<PageResult<ErpMaterialStockRespVO>> getMaterialStockPage() {
        PageResult<ErpMaterialStockDO> pageResult = materialStockService.getMaterialStockPage();
        
        if (CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }
        
        Map<Long, ErpMaterialDO> materialMap = materialService.getMaterialMap(
                convertList(pageResult.getList(), ErpMaterialStockDO::getMaterialId));
        Map<Long, ErpWarehouseDO> warehouseMap = warehouseService.getWarehouseMap(
                convertList(pageResult.getList(), ErpMaterialStockDO::getWarehouseId));
        
        return success(BeanUtils.toBean(pageResult, ErpMaterialStockRespVO.class, stock -> {
            MapUtils.findAndThen(materialMap, stock.getMaterialId(), 
                    material -> {
                        stock.setMaterialName(material.getName());
                        stock.setMaterialCode(material.getCode());
                    });
            MapUtils.findAndThen(warehouseMap, stock.getWarehouseId(), 
                    warehouse -> stock.setWarehouseName(warehouse.getName()));
        }));
    }

}
