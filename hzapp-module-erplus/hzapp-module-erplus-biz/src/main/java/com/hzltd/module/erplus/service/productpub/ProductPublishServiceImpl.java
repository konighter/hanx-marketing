package com.hzltd.module.erplus.service.productpub;

import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishResponse;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishV2Request;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishTaskVO;
import com.hzltd.module.erplus.dal.dataobject.productpub.ErpProductPublishTaskDO;
import com.hzltd.module.erplus.dal.dataobject.productpub.ProductListingDO;
import com.hzltd.module.erplus.spapi.enums.CrossProductPublishStatus;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;

import static com.hzltd.framework.common.util.json.JsonUtils.toJsonString;

@Slf4j
@Service
public class ProductPublishServiceImpl implements ProductPublishService {

    @Resource
    private ErpProductPublishTaskService productPublishTaskService;

    @Resource
    private ProductListingService productListingService;

    public ProductPublishResponse publishProduct(ProductPublishRequest request, Long operatorId) {
        // ... (existing logic)
        return null;
    }

    @Override
    public ProductPublishResponse publishProductV2(ProductPublishV2Request request, Long operatorId) {
        // 1. 获取对应平台的枚举
        CrossPlatformEnum platform = CrossPlatformEnum.of(request.getPlatform());

        // 2. 获取或创建刊登记录 (根据平台/店铺/市场识别唯一性)
        ProductListingDO listing = productListingService.getOrCreateListing(
                platform.getValue(), request.getShopId(), request.getMarketId(), request.getSellerSku());

        // 3. 更新基础属性 (如果需要可以在这里同步一些通用字段到 listing 表)
        
        // 4. 创建发布任务
        ErpProductPublishTaskDO taskDO = ErpProductPublishTaskDO.builder()
                .listingId(listing.getId())
                .listingData(toJsonString(request))
                .status(CrossProductPublishStatus.AUDITING.getStatus()) // 待提交状态
                .build();

        Long taskId = productPublishTaskService.createProductPublishTask(taskDO);

        // 5. 立即提交任务到队列/线程池执行
        productPublishTaskService.submitProductPublishTask(taskId);

        // 6. 构造返回结果
        ProductPublishTaskVO taskVO = new ProductPublishTaskVO()
                .setTaskId(taskId)
                .setProductId(listing.getProductId())
                .setStatus(CrossProductPublishStatus.of(taskDO.getStatus()));

        ProductPublishResponse response = new ProductPublishResponse();
        response.setProductPublishTasks(Collections.singletonList(taskVO));
        return response;
    }

}
