package com.hzltd.module.erplus.adv.metadata.controller.admin.ad;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdDO;
import com.hzltd.module.erplus.adv.metadata.service.ad.AdsAdService;
import com.hzltd.module.erplus.adv.metadata.vo.ad.AdsAdPageReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.ad.AdsAdRespVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 广告")
@RestController
@RequestMapping("/erplus/adv/ad")
@Validated
public class AdsAdController {

    @Resource
    private AdsAdService adsAdService;

    @PostMapping("/page")
    @Operation(summary = "获得广告分页")
    @PreAuthorize("@ss.hasPermission('erplus:adv-ad:query')")
    public CommonResult<PageResult<AdsAdRespVO>> getAdPage(@Valid @RequestBody AdsAdPageReqVO pageReqVO) {
        PageResult<AdsAdDO> pageResult = adsAdService.getAdPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AdsAdRespVO.class));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新广告状态")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true),
            @Parameter(name = "status", description = "统一状态", required = true)
    })
    @PreAuthorize("@ss.hasPermission('erplus:adv-ad:update')")
    public CommonResult<Boolean> updateAdStatus(@RequestParam("id") Long id,
                                                @RequestParam("status") String status) {
        adsAdService.updateAdStatus(id, status);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得广告")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:adv-ad:query')")
    public CommonResult<AdsAdRespVO> getAd(@RequestParam("id") Long id) {
        AdsAdDO ad = adsAdService.getAd(id);
        return success(BeanUtils.toBean(ad, AdsAdRespVO.class));
    }

}
