package com.hzltd.module.amz.adv.controller.admin;

import com.hzltd.framework.common.pojo.CommonResult;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvBidStrategyPageReqVO;
import com.hzltd.module.amz.controller.admin.vo.AmzAdvBidStrategySaveReqVO;
import com.hzltd.module.amz.dal.dataobject.AmzAdvBidStrategyDO;
import com.hzltd.module.amz.dal.mapper.AmzAdvCampaignMapper;
import com.hzltd.module.amz.service.AmzAdvBidStrategyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import static com.hzltd.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 亚马逊广告运营")
@RestController
@RequestMapping("/erplus/amz-adv-operation")
public class AmzAdvOperationController {



    @Resource
    private AmzAdvBidStrategyService amzAdvBidStrategyService;
    @Resource
    private AmzAdvCampaignMapper amzAdvCampaignMapper;






    // ========== 出价策略管理 ==========
    
    @PostMapping("/bid-strategy/create")
    @Operation(summary = "创建出价策略")
    public CommonResult<Long> createBidStrategy(@RequestBody @Valid AmzAdvBidStrategySaveReqVO createReqVO) {
        return success(amzAdvBidStrategyService.createAmzAdvBidStrategy(createReqVO));
    }

    @PutMapping("/bid-strategy/update")
    @Operation(summary = "更新出价策略")
    public CommonResult<Boolean> updateBidStrategy(@RequestBody @Valid AmzAdvBidStrategySaveReqVO updateReqVO) {
        amzAdvBidStrategyService.updateAmzAdvBidStrategy(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/bid-strategy/delete/{id}")
    @Operation(summary = "删除出价策略")
    public CommonResult<Boolean> deleteBidStrategy(@PathVariable("id") Long id) {
        amzAdvBidStrategyService.deleteAmzAdvBidStrategy(id);
        return success(true);
    }

    @GetMapping("/bid-strategy/get/{id}")
    @Operation(summary = "获取出价策略")
    public CommonResult<AmzAdvBidStrategyDO> getBidStrategy(@PathVariable("id") Long id) {
        return success(amzAdvBidStrategyService.getAmzAdvBidStrategy(id));
    }

    @GetMapping("/bid-strategy/page")
    @Operation(summary = "分页获取出价策略")
    public CommonResult<PageResult<AmzAdvBidStrategyDO>> getBidStrategyPage(@Valid AmzAdvBidStrategyPageReqVO pageReqVO) {
        return success(amzAdvBidStrategyService.getAmzAdvBidStrategyPage(pageReqVO));
    }
}