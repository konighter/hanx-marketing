package com.hzltd.module.erplus.controller.admin.shop;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.framework.operatelog.core.annotations.OperateLog;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopPageReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopRespVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.service.shop.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;
import static com.hzltd.framework.operatelog.core.enums.OperateTypeEnum.EXPORT;

@Tag(name = "管理后台 - 店铺信息")
@RestController
@RequestMapping("/ov/shop")
@Validated
public class ShopController {

    @Resource
    private ShopService shopService;

    @PostMapping("/create")
    @Operation(summary = "创建店铺信息")
    @PreAuthorize("@ss.hasPermission('ov:shop:create')")
    public CommonResult<Integer> createShop(@Valid @RequestBody ShopSaveReqVO createReqVO) {
        return success(shopService.createShop(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新店铺信息")
    @PreAuthorize("@ss.hasPermission('ov:shop:update')")
    public CommonResult<Boolean> updateShop(@Valid @RequestBody ShopSaveReqVO updateReqVO) {
        shopService.updateShop(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除店铺信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ov:shop:delete')")
    public CommonResult<Boolean> deleteShop(@RequestParam("id") Integer id) {
        shopService.deleteShop(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得店铺信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ov:shop:query')")
    public CommonResult<ShopRespVO> getShop(@RequestParam("id") Integer id) {
        ShopDO shop = shopService.getShop(id);
        return success(BeanUtils.toBean(shop, ShopRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得店铺信息分页")
    @PreAuthorize("@ss.hasPermission('ov:shop:query')")
    public CommonResult<PageResult<ShopRespVO>> getShopPage(@Valid ShopPageReqVO pageReqVO) {
        PageResult<ShopRespVO> pageResult = shopService.getShopPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/list")
    @Operation(summary = "获得店铺信息列表")
    @PreAuthorize("@ss.hasPermission('ov:shop:query')")
    public CommonResult<List<ShopRespVO>> getShopPage(@Valid ShopReqVO reqVO) {
        List<ShopRespVO> result = shopService.getShopList(reqVO);
        return success(result);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出店铺信息 Excel")
    @PreAuthorize("@ss.hasPermission('ov:shop:export')")
    @OperateLog(type = EXPORT)
    public void exportShopExcel(@Valid ShopPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ShopRespVO> list = shopService.getShopPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "店铺信息.xls", "数据", ShopRespVO.class,
                        list);
    }

}