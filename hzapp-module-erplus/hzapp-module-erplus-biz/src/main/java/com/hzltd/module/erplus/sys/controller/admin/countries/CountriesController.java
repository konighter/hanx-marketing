package com.hzltd.module.erplus.sys.controller.admin.countries;

import com.hzltd.framework.apilog.core.annotation.ApiAccessLog;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.framework.excel.core.util.ExcelUtils;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesRespVO;
import com.hzltd.module.erplus.sys.controller.admin.countries.vo.CountriesSaveReqVO;
import com.hzltd.module.erplus.sys.dal.dataobject.countries.CountriesDO;
import com.hzltd.module.erplus.sys.service.countries.CountriesService;
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

@Tag(name = "管理后台 - [Erplus] 国家/地区定义")
@RestController
@RequestMapping("/erplus/countries")
@Validated
public class CountriesController {

    @Resource
    private CountriesService countriesService;

    @PostMapping("/create")
    @Operation(summary = "创建[Erplus] 国家/地区定义")
    @PreAuthorize("@ss.hasPermission('erplus:countries:create')")
    public CommonResult<Integer> createCountries(@Valid @RequestBody CountriesSaveReqVO createReqVO) {
        return success(countriesService.createCountries(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新[Erplus] 国家/地区定义")
    @PreAuthorize("@ss.hasPermission('erplus:countries:update')")
    public CommonResult<Boolean> updateCountries(@Valid @RequestBody CountriesSaveReqVO updateReqVO) {
        countriesService.updateCountries(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除[Erplus] 国家/地区定义")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:countries:delete')")
    public CommonResult<Boolean> deleteCountries(@RequestParam("id") Integer id) {
        countriesService.deleteCountries(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得[Erplus] 国家/地区定义")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:countries:query')")
    public CommonResult<CountriesRespVO> getCountries(@RequestParam("id") Integer id) {
        CountriesDO countries = countriesService.getCountries(id);
        return success(BeanUtils.toBean(countries, CountriesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得[Erplus] 国家/地区定义分页")
    @PreAuthorize("@ss.hasPermission('erplus:countries:query')")
    public CommonResult<PageResult<CountriesRespVO>> getCountriesPage(@Valid CountriesPageReqVO pageReqVO) {
        PageResult<CountriesDO> pageResult = countriesService.getCountriesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CountriesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出[Erplus] 国家/地区定义 Excel")
    @PreAuthorize("@ss.hasPermission('erplus:countries:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCountriesExcel(@Valid CountriesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CountriesDO> list = countriesService.getCountriesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "[Erplus] 国家/地区定义.xls", "数据", CountriesRespVO.class,
                        BeanUtils.toBean(list, CountriesRespVO.class));
    }

}