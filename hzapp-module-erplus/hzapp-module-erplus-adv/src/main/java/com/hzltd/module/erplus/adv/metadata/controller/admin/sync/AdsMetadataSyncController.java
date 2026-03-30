package com.hzltd.module.erplus.adv.metadata.controller.admin.sync;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.module.erplus.adv.metadata.service.sync.AdsMetadataSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 广告元数据同步")
@RestController
@RequestMapping("/erplus/adv/sync")
@Validated
public class AdsMetadataSyncController {

    @Resource
    private AdsMetadataSyncService adsMetadataSyncService;

    @PostMapping("/metadata-all")
    @Operation(summary = "全量同步广告元数据")
    @Parameter(name = "accountId", description = "账号编号", required = false)
    @Parameter(name = "shopId", description = "店铺编号", required = false)
    @PreAuthorize("@ss.hasPermission('erplus:adv-sync:metadata')")
    public CommonResult<Boolean> syncAllMetadata(@RequestParam(value = "accountId", required = false) Long accountId,
                                                @RequestParam(value = "shopId", required = false) Long shopId) {
        if (shopId != null) {
            adsMetadataSyncService.syncAllMetadataByShop(shopId);
        } else if (accountId != null) {
            adsMetadataSyncService.syncAllMetadata(accountId);
        }
        return success(true);
    }

    @PostMapping("/metadata-incr")
    @Operation(summary = "增量同步广告元数据")
    @Parameter(name = "accountId", description = "账号编号", required = false)
    @Parameter(name = "shopId", description = "店铺编号", required = false)
    @PreAuthorize("@ss.hasPermission('erplus:adv-sync:metadata')")
    public CommonResult<Boolean> syncIncrementalMetadata(@RequestParam(value = "accountId", required = false) Long accountId,
                                                       @RequestParam(value = "shopId", required = false) Long shopId) {
        if (shopId != null) {
            adsMetadataSyncService.syncIncrementalMetadataByShop(shopId);
        } else if (accountId != null) {
            adsMetadataSyncService.syncIncrementalMetadata(accountId);
        }
        return success(true);
    }

}
