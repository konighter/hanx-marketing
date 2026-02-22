package com.hzltd.module.erplus.adv.metadata.controller.admin.adgroup;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.adv.dal.dataobject.AdsAdGroupDO;
import com.hzltd.module.erplus.adv.metadata.service.adgroup.AdsAdGroupService;
import com.hzltd.module.erplus.adv.metadata.vo.adgroup.AdsAdGroupPageReqVO;
import com.hzltd.module.erplus.adv.metadata.vo.adgroup.AdsAdGroupRespVO;
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

@Tag(name = "管理后台 - 广告组")
@RestController
@RequestMapping("/erplus/adv/ad-group")
@Validated
public class AdsAdGroupController {

    @Resource
    private AdsAdGroupService adsAdGroupService;

    @GetMapping("/page")
    @Operation(summary = "获得广告组分页")
    @PreAuthorize("@ss.hasPermission('erplus:adv-ad-group:query')")
    public CommonResult<PageResult<AdsAdGroupRespVO>> getAdGroupPage(@Valid AdsAdGroupPageReqVO pageReqVO) {
        PageResult<AdsAdGroupDO> pageResult = adsAdGroupService.getAdGroupPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AdsAdGroupRespVO.class));
    }

    @PutMapping("/update-status")
    @Operation(summary = "更新广告组状态")
    @Parameters({
            @Parameter(name = "id", description = "编号", required = true),
            @Parameter(name = "status", description = "统一状态", required = true)
    })
    @PreAuthorize("@ss.hasPermission('erplus:adv-ad-group:update')")
    public CommonResult<Boolean> updateAdGroupStatus(@RequestParam("id") Long id,
                                                     @RequestParam("status") String status) {
        adsAdGroupService.updateAdGroupStatus(id, status);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得广告组")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:adv-ad-group:query')")
    public CommonResult<AdsAdGroupRespVO> getAdGroup(@RequestParam("id") Long id) {
        AdsAdGroupDO adGroup = adsAdGroupService.getAdGroup(id);
        return success(BeanUtils.toBean(adGroup, AdsAdGroupRespVO.class));
    }

}
