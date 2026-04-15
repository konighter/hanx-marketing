package com.hzltd.module.erplus.controller.admin.shop;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopPageReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopReqVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopRespVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.ShopSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.shop.ShopDO;
import com.hzltd.module.erplus.service.shop.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.hzltd.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static com.hzltd.framework.common.pojo.CommonResult.success;


@Tag(name = "管理后台 - 店铺信息")
@RestController
@RequestMapping("/erplus/cross/shop")
@Validated
public class CrossShopController {

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
        ShopRespVO respVO = BeanUtils.toBean(shop, ShopRespVO.class);
        hideSensitiveAuthInfo(respVO);
        return success(respVO);
    }

    @GetMapping("/platform")
    @Operation(summary = "获得店铺信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ov:shop:query')")
    public CommonResult<List<ShopRespVO>> getShopByPlatform(@RequestParam("id") Integer platformId) {
        List<ShopDO> shop = shopService.getShopListByPlatform(platformId);
        List<ShopRespVO> list = BeanUtils.toBean(shop, ShopRespVO.class);
        hideSensitiveAuthInfo(list);
        return success(list);
    }

    @GetMapping("/page")
    @Operation(summary = "获得店铺信息分页")
    @PreAuthorize("@ss.hasPermission('ov:shop:query')")
    public CommonResult<PageResult<ShopRespVO>> getShopPage(@Valid ShopPageReqVO pageReqVO) {
        PageResult<ShopRespVO> pageResult = shopService.getShopPage(pageReqVO);
        if (pageResult != null) {
            hideSensitiveAuthInfo(pageResult.getList());
        }
        return success(pageResult);
    }

    @GetMapping("/list")
    @Operation(summary = "获得店铺信息列表")
    @PreAuthorize("@ss.hasPermission('ov:shop:query')")
    public CommonResult<List<ShopRespVO>> getShopPage(@Valid ShopReqVO reqVO) {
        List<ShopRespVO> result = shopService.getShopList(reqVO);
        hideSensitiveAuthInfo(result);
        return success(result);
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出店铺信息 Excel")
    @PreAuthorize("@ss.hasPermission('ov:shop:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportShopExcel(@Valid ShopPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ShopRespVO> list = shopService.getShopPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "店铺信息.xls", "数据", ShopRespVO.class,
                        list);
    }

//
//    @PostMapping("/auth")
//    @Operation(summary = "获得店铺授权信息")
//    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('ov:shop:query')")
//    public CommonResult<ShopAuthRespVO> submitShopAuth(@RequestBody ShopAuthReqVO authReqVO) {
//        ShopAuthRespVO shopAuthRespVO = shopService.submitShopAuth(authReqVO);
//        return success(shopAuthRespVO);
//    }



    @GetMapping("/cascader-list")
    @Operation(summary = "获得店铺级联信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ov:shop:query')")
    public CommonResult<?> cascaderShop() {
        return success(shopService.getCascaderShopList());
    }

    private void hideSensitiveAuthInfo(ShopRespVO respVO) {
        if (respVO != null && respVO.getAuthInfo() != null) {
            respVO.getAuthInfo().setAccessToken(null);
        }
    }

    private void hideSensitiveAuthInfo(List<ShopRespVO> list) {
        if (list != null) {
            for (ShopRespVO respVO : list) {
                hideSensitiveAuthInfo(respVO);
            }
        }
    }

}