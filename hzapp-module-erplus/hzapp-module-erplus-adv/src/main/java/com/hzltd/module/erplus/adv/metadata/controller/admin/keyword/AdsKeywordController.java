package com.hzltd.module.erplus.adv.metadata.controller.admin.keyword;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsKeywordDO;
import com.hzltd.module.erplus.adv.metadata.service.keyword.AdsKeywordService;
import com.hzltd.module.erplus.adv.metadata.vo.keyword.AdsKeywordPageReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.keyword.AdsKeywordRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 广告关键词")
@RestController
@RequestMapping("/erplus/adv/keyword")
@Validated
public class AdsKeywordController {

    @Resource
    private AdsKeywordService adsKeywordService;

    @PostMapping("/page")
    @Operation(summary = "获得广告关键词分页")
    @PreAuthorize("@ss.hasPermission('erplus:adv-keyword:query')")
    public CommonResult<PageResult<AdsKeywordRespVO>> getKeywordPage(@Valid @RequestBody AdsKeywordPageReqVO pageReqVO) {
        PageResult<AdsKeywordDO> pageResult = adsKeywordService.getKeywordPage(pageReqVO);
        PageResult<AdsKeywordRespVO> result = BeanUtils.toBean(pageResult, AdsKeywordRespVO.class);
        // 聚合属性数据
        if (result != null && !CollectionUtils.isEmpty(result.getList())) {
            result.getList().forEach(vo -> vo.setAttributes(adsKeywordService.getKeywordAttributes(vo.getId())));
        }
        return success(result);
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新广告关键词状态")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true),
            @Parameter(name = "status", description = "统一状态", required = true)
    })
    @PreAuthorize("@ss.hasPermission('erplus:adv-keyword:update')")
    public CommonResult<Boolean> updateKeywordStatus(@RequestParam("id") Long id,
                                                     @RequestParam("status") String status) {
        adsKeywordService.updateKeywordStatus(id, status);
        return success(true);
    }

    @PutMapping("/update-bid")
    @Operation(summary = "更新广告关键词出价")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true),
            @Parameter(name = "bid", description = "出价", required = true)
    })
    @PreAuthorize("@ss.hasPermission('erplus:adv-keyword:update')")
    public CommonResult<Boolean> updateKeywordBid(@RequestParam("id") Long id,
                                                  @RequestParam("bid") java.math.BigDecimal bid) {
        adsKeywordService.updateKeywordBid(id, bid);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得广告关键词")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:adv-keyword:query')")
    public CommonResult<AdsKeywordRespVO> getKeyword(@RequestParam("id") Long id) {
        AdsKeywordDO keyword = adsKeywordService.getKeyword(id);
        AdsKeywordRespVO respVO = BeanUtils.toBean(keyword, AdsKeywordRespVO.class);
        if (respVO != null) {
            respVO.setAttributes(adsKeywordService.getKeywordAttributes(id));
        }
        return success(respVO);
    }

}
