package com.hzltd.module.erplus.service.productpub;
 
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hzltd.framework.common.exception.ServiceException;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.api.service.ProductApiFactory;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishV2Request;
import com.hzltd.module.erplus.dal.dataobject.productpub.ErpProductPublishTaskDO;
import com.hzltd.module.erplus.dal.dataobject.productpub.ProductListingDO;
import com.hzltd.module.erplus.dal.dataobject.sellplatform.SellPlatformDO;
import com.hzltd.module.erplus.dal.mysql.productpub.ErpProductPublishTaskMapper;
import com.hzltd.module.erplus.service.cross.ErplusCrossProductService;
import com.hzltd.module.erplus.service.executor.ExecutorService;
import com.hzltd.module.erplus.service.sellplatform.SellPlatformService;
import com.hzltd.module.erplus.service.shop.ShopService;
import com.hzltd.module.erplus.spapi.enums.CrossProductPublishStatus;
import com.hzltd.module.erplus.spapi.model.ApiRequest;
import com.hzltd.module.erplus.spapi.model.ApiResponse;
import com.hzltd.module.erplus.spapi.model.category.CategoryModel;
import com.hzltd.module.erplus.spapi.model.common.Image;
import com.hzltd.module.erplus.spapi.model.common.SkuModel;
import com.hzltd.module.erplus.spapi.model.product.CreateProductRequest;
import com.hzltd.module.erplus.spapi.model.product.CreateProductResponse;
import com.hzltd.module.erplus.spapi.service.product.ProductApi;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
 
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
 
import static com.hzltd.module.erplus.system.enums.ErplusErrorCodeConstants.PRODUCT_NOT_EXISTS;
 
/**
 * 商品发布任务 Service 实现类
 *
 * @author Antigravity
 */
@Slf4j
@Service
@Validated
public class ErpProductPublishTaskServiceImpl implements ErpProductPublishTaskService {
 
    @Resource
    private ErpProductPublishTaskMapper erpProductPublishTaskMapper;
 
    @Resource
    private ProductListingService productListingService;
 
    @Resource
    private ProductApiFactory productApiFactory;
 
    @Resource
    private SellPlatformService platformService;
 
    @Resource
    private ShopService shopService;
 
    @Resource
    @Lazy
    private ErplusCrossProductService crossProductService;
 
    @Override
    public Optional<ErpProductPublishTaskDO> getProductPublishTask(Long taskId) {
        return Optional.ofNullable(erpProductPublishTaskMapper.selectById(taskId));
    }
 
    @Override
    public Long createProductPublishTask(ErpProductPublishTaskDO task) {
        erpProductPublishTaskMapper.insert(task);
        return task.getId();
    }
 
    @Override
    public void updateProductPublishTask(ErpProductPublishTaskDO task) {
        erpProductPublishTaskMapper.updateById(task);
    }
 
    @Override
    public java.util.List<ErpProductPublishTaskDO> getPendingPublishTasks() {
        return erpProductPublishTaskMapper.selectList(new LambdaQueryWrapper<ErpProductPublishTaskDO>()
                .eq(ErpProductPublishTaskDO::getStatus, CrossProductPublishStatus.AUDITING.getStatus())
                .le(ErpProductPublishTaskDO::getScheduleTime, java.time.LocalDateTime.now()) // 调度时间小于等于当前时间
        );
    }
 
    @Override
    public boolean submitProductPublishTask(Long taskId) {
        Optional<ErpProductPublishTaskDO> taskOpt = getProductPublishTask(taskId);
        if (taskOpt.isPresent()) {
            ErpProductPublishTaskDO task = taskOpt.get();
            // 同步更新为发布中，防止重复提交
            task.setStatus(CrossProductPublishStatus.PUBLISHING.getStatus());
            task.setBeginTime(LocalDateTime.now());
            updateProductPublishTask(task);
        }
 
        ListenableFuture<PublishTaskExecuteResult> future = ExecutorService.submitProductPublishTask(new ProductPublishTask(taskId));
        Futures.addCallback(future, new ProductPublishCallbackListener(taskId), ExecutorService.getCallbackExecutorService());
        return true;
    }
 
    @AllArgsConstructor
    public static class PublishTaskExecuteResult {
        ApiResponse<CreateProductResponse> response;
    }
 
    @AllArgsConstructor
    public class ProductPublishTask implements Callable<PublishTaskExecuteResult> {
 
        private Long taskId;
 
        @Override
        public PublishTaskExecuteResult call() throws Exception {
            Optional<ErpProductPublishTaskDO> publishTaskOpt = getProductPublishTask(taskId);
            if (!publishTaskOpt.isPresent()) {
                throw new ServiceException(PRODUCT_NOT_EXISTS);
            }
            ErpProductPublishTaskDO publishTask = publishTaskOpt.get();
            ProductListingDO listing = productListingService.getListing(publishTask.getListingId());
            if (listing == null) {
                throw new ServiceException(PRODUCT_NOT_EXISTS);
            }
 
            SellPlatformDO platform = platformService.getSellPlatform(listing.getPlatformId());
 
            ProductApi crossServiceCompositeApi = productApiFactory.getCrossApiService(CrossPlatformEnum.of(platform.getCode()));
 
            CreateProductRequest request;
            if (StrUtil.isNotEmpty(publishTask.getListingData())) {
                request = buildRequestFromListingData(publishTask, listing);
            } else {
                // 原有的 buildCrossCreateProductRequest 逻辑在 V2 流程中目前是占位符
                request = null; 
            }
 
            ApiResponse<CreateProductResponse> response = crossServiceCompositeApi.createProduct(new ApiRequest<CreateProductRequest>().setShopIdInt(listing.getShopId()).setRequest(request));
            return new PublishTaskExecuteResult(response);
        }
 
        private CreateProductRequest buildRequestFromListingData(ErpProductPublishTaskDO task, ProductListingDO listing) {
            ProductPublishV2Request v2Request = JsonUtils.parseObject(task.getListingData(), ProductPublishV2Request.class);
            
            CreateProductRequest request = new CreateProductRequest();
            request.setShopId(listing.getShopId());
            request.setCrossPlatform(CrossPlatformEnum.valueOf(listing.getPlatformId()));
            request.setMarketId(v2Request.getMarketId());
            request.setCategory(new CategoryModel().setCategoryId(v2Request.getProductType()));
            // 基础信息映射
            request.setTitle(v2Request.getTitle());
            request.setDescription(v2Request.getDescription());
            
            // 主图映射
            if (StrUtil.isNotEmpty(v2Request.getMainImage())) {
                Image mainImg = new Image();
                mainImg.setUrl(v2Request.getMainImage());
                request.setMainImage(mainImg);
            }
            
            // 附图映射
            if (v2Request.getAdditionalImages() != null && !v2Request.getAdditionalImages().isEmpty()) {
                request.setSliderImages(v2Request.getAdditionalImages().stream().map(url -> {
                    Image img = new Image();
                    img.setUrl(url);
                    return img;
                }).collect(java.util.stream.Collectors.toList()));
            }
            
            // SKU 映射
            SkuModel skuModel = new SkuModel();
            skuModel.setSellerSku(v2Request.getSellerSku());
            request.setSkus(List.of(skuModel));
            
            // 动态属性映射
            request.setCrossPlatformExtAttrs(v2Request.getAttributes());
            
            return request;
        }
    }
 
    @AllArgsConstructor
    private class ProductPublishCallbackListener implements FutureCallback<PublishTaskExecuteResult> {
 
        private Long taskId;
 
        @Override
        public void onSuccess(PublishTaskExecuteResult result) {
            log.info("ProductPublishSuccess: taskId={}, response={}", taskId, result.response);
            
            // 更新任务状态和反馈
            Optional<ErpProductPublishTaskDO> taskOpt = getProductPublishTask(taskId);
            if (taskOpt.isPresent()) {
                ErpProductPublishTaskDO task = taskOpt.get();
                boolean isSuccess = result.response.success();
                task.setStatus(isSuccess ? CrossProductPublishStatus.PUBLISH_SUC.getStatus() : CrossProductPublishStatus.PUBLISH_FAIL.getStatus());
                
                task.setEndTime(java.time.LocalDateTime.now());
                
                // 设置详细反馈
                String feedbackJson = JsonUtils.toJsonString(result.response);
                task.setRawFeedback(feedbackJson);
                
                // 解析简要错误
                if (!isSuccess && result.response != null) {
                    task.setBrief(result.response.getMsg());
                    task.setStatusInfo(result.response.getMsg());
                } else if (isSuccess) {
                    task.setBrief("Success");
                    task.setStatusInfo("Publishing Successful");
                }
                
                updateProductPublishTask(task);
                
                // 更新 Listing 状态 (10:发布中 -> 99:成功, 91:失败)
                productListingService.updateListingStatus(task.getListingId(), isSuccess ? CrossProductPublishStatus.PUBLISH_SUC.getStatus() : CrossProductPublishStatus.PUBLISH_FAIL.getStatus(), taskId);
 
                // Promotion Logic: 成功后同步到正式商品表
                if (isSuccess) {
                    crossProductService.syncCrossproductV2(taskId);
                }
            }
        }
 
        @Override
        public void onFailure(@NotNull Throwable t) {
            log.error("ProductPublishError: taskId={}", this.taskId, t);
            
            Optional<ErpProductPublishTaskDO> taskOpt = getProductPublishTask(taskId);
            if (taskOpt.isPresent()) {
                ErpProductPublishTaskDO task = taskOpt.get();
                task.setStatus(CrossProductPublishStatus.PUBLISH_FAIL.getStatus()); // 失败
                task.setBrief(t.getMessage());
                task.setEndTime(java.time.LocalDateTime.now());
                updateProductPublishTask(task);
                
                productListingService.updateListingStatus(task.getListingId(), 91, taskId);
            }
        }
    }
}
