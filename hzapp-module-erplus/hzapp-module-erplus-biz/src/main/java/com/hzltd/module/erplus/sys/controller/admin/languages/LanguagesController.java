package com.hzltd.module.erplus.sys.controller.admin.languages;

import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesPageReqVO;
import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesRespVO;
import com.hzltd.module.erplus.sys.controller.admin.languages.vo.LanguagesSaveReqVO;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import static com.hzltd.framework.common.pojo.CommonResult.success;

import com.hzltd.framework.excel.core.util.ExcelUtils;

import com.hzltd.framework.operatelog.core.annotations.OperateLog;
import static com.hzltd.framework.operatelog.core.enums.OperateTypeEnum.*;

import com.hzltd.module.erplus.sys.controller.admin.languages.vo.*;
import com.hzltd.module.erplus.sys.dal.dataobject.languages.LanguagesDO;
import com.hzltd.module.erplus.sys.service.languages.LanguagesService;

@Tag(name = "管理后台 - [Erplus] 语言定义")
@RestController
@RequestMapping("/erplus/languages")
@Validated
public class LanguagesController {

    @Resource
    private LanguagesService languagesService;

    @PostMapping("/create")
    @Operation(summary = "创建[Erplus] 语言定义")
    @PreAuthorize("@ss.hasPermission('erplus:languages:create')")
    public CommonResult<Integer> createLanguages(@Valid @RequestBody LanguagesSaveReqVO createReqVO) {
        return success(languagesService.createLanguages(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新[Erplus] 语言定义")
    @PreAuthorize("@ss.hasPermission('erplus:languages:update')")
    public CommonResult<Boolean> updateLanguages(@Valid @RequestBody LanguagesSaveReqVO updateReqVO) {
        languagesService.updateLanguages(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除[Erplus] 语言定义")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:languages:delete')")
    public CommonResult<Boolean> deleteLanguages(@RequestParam("id") Integer id) {
        languagesService.deleteLanguages(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得[Erplus] 语言定义")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:languages:query')")
    public CommonResult<LanguagesRespVO> getLanguages(@RequestParam("id") Integer id) {
        LanguagesDO languages = languagesService.getLanguages(id);
        return success(BeanUtils.toBean(languages, LanguagesRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得[Erplus] 语言定义分页")
    @PreAuthorize("@ss.hasPermission('erplus:languages:query')")
    public CommonResult<PageResult<LanguagesRespVO>> getLanguagesPage(@Valid LanguagesPageReqVO pageReqVO) {
        PageResult<LanguagesDO> pageResult = languagesService.getLanguagesPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LanguagesRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出[Erplus] 语言定义 Excel")
    @PreAuthorize("@ss.hasPermission('erplus:languages:export')")
    @OperateLog(type = EXPORT)
    public void exportLanguagesExcel(@Valid LanguagesPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<LanguagesDO> list = languagesService.getLanguagesPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "[Erplus] 语言定义.xls", "数据", LanguagesRespVO.class,
                        BeanUtils.toBean(list, LanguagesRespVO.class));
    }

}