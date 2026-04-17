package com.hzltd.module.erplus.controller.admin.material;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialPageReqVO;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialRespVO;
import com.hzltd.module.erplus.controller.admin.material.vo.material.ErpMaterialSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.material.ErpMaterialDO;
import com.hzltd.module.erplus.service.material.ErpMaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.apilog.core.enums.OperateTypeEnum.*;
import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - ERP 耗材")
@RestController
@RequestMapping("/erplus/material")
@Validated
public class ErpMaterialController {

    @Resource
    private ErpMaterialService materialService;

    @Resource
    private com.hzltd.module.erplus.service.spu.ProductCodeService productCodeService;

    @GetMapping("/generate-code")
    @Operation(summary = "获得耗材建议编码")
    @Parameter(name = "name", description = "耗材名称", example = "纸箱")
    public CommonResult<String> generateCode(@RequestParam("name") String name) {
        return success(productCodeService.generateMaterialCode(name));
    }

    @PostMapping("/create")
    @Operation(summary = "创建耗材")
    @PreAuthorize("@ss.hasPermission('erp:material:create')")
    @ApiAccessLog(operateType = CREATE)
    public CommonResult<Long> createMaterial(@Valid @RequestBody ErpMaterialSaveReqVO reqVO) {
        return success(materialService.createMaterial(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新耗材")
    @PreAuthorize("@ss.hasPermission('erp:material:update')")
    @ApiAccessLog(operateType = UPDATE)
    public CommonResult<Boolean> updateMaterial(@Valid @RequestBody ErpMaterialSaveReqVO reqVO) {
        materialService.updateMaterial(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除耗材")
    @Parameter(name = "id", description = "编号", example = "1")
    @PreAuthorize("@ss.hasPermission('erp:material:delete')")
    @ApiAccessLog(operateType = DELETE)
    public CommonResult<Boolean> deleteMaterial(@RequestParam("id") Long id) {
        materialService.deleteMaterial(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得耗材")
    @Parameter(name = "id", description = "编号", example = "1")
    public CommonResult<ErpMaterialRespVO> getMaterial(@RequestParam("id") Long id) {
        ErpMaterialDO material = materialService.getMaterial(id);
        ErpMaterialRespVO respVO = BeanUtils.toBean(material, ErpMaterialRespVO.class);
        if (respVO != null) {
            respVO.setTotalCount(materialService.getMaterialStockCount(id));
        }
        return success(respVO);
    }

    @GetMapping("/page")
    @Operation(summary = "获得耗材分页")
    @PreAuthorize("@ss.hasPermission('erp:material:query')")
    public CommonResult<PageResult<ErpMaterialRespVO>> getMaterialPage(@Valid ErpMaterialPageReqVO pageReqVO) {
        PageResult<ErpMaterialDO> pageResult = materialService.getMaterialPage(pageReqVO);
        if (cn.hutool.core.collection.CollUtil.isEmpty(pageResult.getList())) {
            return success(PageResult.empty());
        }

        // 批量填充库存
        java.util.Map<Long, java.math.BigDecimal> stockMap = materialService.getMaterialStockCountMap(
                com.hzltd.framework.common.util.collection.CollectionUtils.convertSet(pageResult.getList(), ErpMaterialDO::getId));
        return success(BeanUtils.toBean(pageResult, ErpMaterialRespVO.class, 
                vo -> vo.setTotalCount(stockMap.getOrDefault(vo.getId(), java.math.BigDecimal.ZERO))));
    }

}
