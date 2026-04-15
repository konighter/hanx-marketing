package com.hzltd.module.amz.adv.controller.admin.manager;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvAdGroupPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvAdGroupSaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvAdGroupDO;
import com.hzltd.module.amz.service.AmzAdvAdGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;



@Tag(name = "管理后台 - 亚马逊广告运营")
@RestController
@RequestMapping("/erplus/amz-adv-operation")
public class AmzAdvAdGroupManagerController {

    @Resource
    private AmzAdvAdGroupService amzAdvAdGroupService;

    // ========== 广告组管理 ==========

    @PostMapping("/ad-group/create")
    @Operation(summary = "创建广告组")
    public CommonResult<Long> createAdGroup(@RequestBody @Valid AmzAdvAdGroupSaveReqVO createReqVO) {
        return success(amzAdvAdGroupService.createAmzAdvAdGroup(createReqVO));
    }

    @PutMapping("/ad-group/update")
    @Operation(summary = "更新广告组")
    public CommonResult<Boolean> updateAdGroup(@RequestBody @Valid AmzAdvAdGroupSaveReqVO updateReqVO) {
        amzAdvAdGroupService.updateAmzAdvAdGroup(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/ad-group/delete/{id}")
    @Operation(summary = "删除广告组")
    public CommonResult<Boolean> deleteAdGroup(@PathVariable("id") Long id) {
        amzAdvAdGroupService.deleteAmzAdvAdGroup(id);
        return success(true);
    }

    @GetMapping("/ad-group/get/{id}")
    @Operation(summary = "获取广告组")
    public CommonResult<AmzAdvAdGroupDO> getAdGroup(@PathVariable("id") Long id) {
        return success(amzAdvAdGroupService.getAmzAdvAdGroup(id));
    }

    @GetMapping("/ad-group/page")
    @Operation(summary = "分页获取广告组")
    public CommonResult<PageResult<AmzAdvAdGroupDO>> getAdGroupPage(@Valid AmzAdvAdGroupPageReqVO pageReqVO) {
        return success(amzAdvAdGroupService.getAmzAdvAdGroupPage(pageReqVO));
    }



}
