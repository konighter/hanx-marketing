package com.hzltd.module.erplus.sys.controller.admin.currencies;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesRespVO;
import com.hzltd.module.erplus.sys.controller.admin.currencies.vo.CurrenciesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.currencies.CurrenciesDO;
import com.hzltd.module.erplus.sys.service.currencies.CurrenciesService;
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

@Tag(name = "管理后台 - [Erplus] 货币定义")
@RestController
@RequestMapping("/erplus/currencies")
@Validated
public class CurrenciesController {

    @Resource
    private CurrenciesService currenciesService;

    @PostMapping("/create")
    @Operation(summary = "创建[Erplus] 货币定义")
    @PreAuthorize("@ss.hasPermission('erplus:currencies:create')")
    public CommonResult<Integer> createCurrencies(@Valid @RequestBody CurrenciesSaveReqVO createReqVO) {
        return success(currenciesService.createCurrencies(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新[Erplus] 货币定义")
    @PreAuthorize("@ss.hasPermission('erplus:currencies:update')")
    public CommonResult<Boolean> updateCurrencies(@Valid @RequestBody CurrenciesSaveReqVO updateReqVO) {
        currenciesService.updateCurrencies(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除[Erplus] 货币定义")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:currencies:delete')")
    public CommonResult<Boolean> deleteCurrencies(@RequestParam("id") Integer id) {
        currenciesService.deleteCurrencies(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得[Erplus] 货币定义")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:currencies:query')")
    public CommonResult<CurrenciesRespVO> getCurrencies(@RequestParam("id") Integer id) {
        CurrenciesDO currencies = currenciesService.getCurrencies(id);
        return success(BeanUtils.toBean(currencies, CurrenciesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得[Erplus] 货币定义分页")
    @PreAuthorize("@ss.hasPermission('erplus:currencies:query')")
    public CommonResult<PageResult<CurrenciesRespVO>> getCurrenciesPage(@Valid CurrenciesPageReqVO pageReqVO) {
        PageResult<CurrenciesDO> pageResult = currenciesService.getCurrenciesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CurrenciesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出[Erplus] 货币定义 Excel")
    @PreAuthorize("@ss.hasPermission('erplus:currencies:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCurrenciesExcel(@Valid CurrenciesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CurrenciesDO> list = currenciesService.getCurrenciesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "[Erplus] 货币定义.xls", "数据", CurrenciesRespVO.class,
                        BeanUtils.toBean(list, CurrenciesRespVO.class));
    }

}