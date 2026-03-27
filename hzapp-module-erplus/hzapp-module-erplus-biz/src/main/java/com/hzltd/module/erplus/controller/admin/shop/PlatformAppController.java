package com.hzltd.module.erplus.controller.admin.shop;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.controller.admin.shop.vo.PlatformAppRespVO;
import com.hzltd.module.erplus.controller.admin.shop.vo.PlatformAppSaveReqVO;
import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAppDO;
import com.hzltd.module.erplus.service.shop.PlatformAppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 平台应用")
@RestController
@RequestMapping("/erplus/platform-app")
@Validated
public class PlatformAppController {

    @Resource
    private PlatformAppService platformAppService;

    @PostMapping("/create")
    @Operation(summary = "创建平台应用")
    @PreAuthorize("@ss.hasPermission('erplus:platform-app:create')")
    public CommonResult<Long> createPlatformApp(@Valid @RequestBody PlatformAppSaveReqVO createReqVO) {
        return success(platformAppService.createPlatformApp(BeanUtils.toBean(createReqVO, PlatformAppDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新平台应用")
    @PreAuthorize("@ss.hasPermission('erplus:platform-app:update')")
    public CommonResult<Boolean> updatePlatformApp(@Valid @RequestBody PlatformAppSaveReqVO updateReqVO) {
        platformAppService.updatePlatformApp(BeanUtils.toBean(updateReqVO, PlatformAppDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除平台应用")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('erplus:platform-app:delete')")
    public CommonResult<Boolean> deletePlatformApp(@RequestParam("id") Long id) {
        platformAppService.deletePlatformApp(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得平台应用")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('erplus:platform-app:query')")
    public CommonResult<PlatformAppRespVO> getPlatformApp(@RequestParam("id") Long id) {
        PlatformAppDO app = platformAppService.getPlatformApp(id);
        return success(BeanUtils.toBean(app, PlatformAppRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得平台应用列表")
    @Parameter(name = "platform", description = "平台类型", example = "AMAZON")
    @PreAuthorize("@ss.hasPermission('erplus:platform-app:query')")
    public CommonResult<List<PlatformAppRespVO>> getPlatformAppList(@RequestParam(value = "platform", required = false) String platform) {
        List<PlatformAppDO> list = platformAppService.getPlatformAppList(platform);
        return success(BeanUtils.toBean(list, PlatformAppRespVO.class));
    }

}
