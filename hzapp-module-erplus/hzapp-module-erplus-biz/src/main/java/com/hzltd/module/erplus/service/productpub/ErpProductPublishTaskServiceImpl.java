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
import com.hzltd.module.erplus.spapi.model.product.*;
import com.hzltd.module.erplus.spapi.service.product.ProductApi;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
                .in(ErpProductPublishTaskDO::getStatus, List.of(CrossProductPublishStatus.AUDITING.getStatus(), CrossProductPublishStatus.PUBLISH_SUBMIT.getStatus()))
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

    @Override
    public boolean submitProductPublishCheckTask(Long taskId) {

        return true;
    }
 
    @AllArgsConstructor
    public static class PublishTaskExecuteResult {
        boolean success;
        CreateProductResponse response;
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
            return new PublishTaskExecuteResult(response.success(), response.getData());
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

                CrossProductPublishStatus status;
                if (result.success) {
                    if (StringUtils.isNotEmpty(result.response.getProductId())) {
                        status = CrossProductPublishStatus.PUBLISH_SUC;
                    } else {
                        status = CrossProductPublishStatus.PUBLISH_SUBMIT;
                        task.setScheduleTime(LocalDateTime.now().plusMinutes(10));
                    }

                } else {
                    status = CrossProductPublishStatus.PUBLISH_FAIL;
                    task.setEndTime(LocalDateTime.now());
                }

                task.setStatus(status.getStatus());

                // 设置详细反馈
                task.setRawFeedback(JsonUtils.toJsonString(result.response));
                
                // 解析简要错误
                if (status.getStatus() < 0) {
                    task.setBrief(JsonUtils.toJsonString(result.response.getIssues()));
                    task.setStatusInfo(status.getName());
                } else {
                    task.setBrief("Success");
                    task.setStatusInfo(status.getName());
                }
                
                updateProductPublishTask(task);
                // 更新 Listing 状态 (10:发布中 -> 99:成功, 91:失败)
                productListingService.updateListingStatus(task.getListingId(), status.getStatus(), taskId, result.response.getProductId());

                if (CrossProductPublishStatus.PUBLISH_SUC.equals(status)) {
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
                
                productListingService.updateListingStatus(task.getListingId(), 91, taskId, null);
            }
        }
    }

    @AllArgsConstructor
    public class ProductCheckTask implements Callable<PublishTaskExecuteResult> {
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

            ApiResponse<MultiMarketProductModel> getProductResp = crossServiceCompositeApi.getProduct(new ApiRequest<GetProductRequest>()
                    .setShopIdInt(listing.getShopId())
                    .setRequest(new GetProductRequest().setSellerSku(listing.getSellerSku())));
            if (getProductResp.success()) {
                ProductModel productModel = getProductResp.getData().get(listing.getMarketId());
                return new PublishTaskExecuteResult(true, new CreateProductResponse()
                        .setProductId(productModel.getProductCode())
                        .setSkuModel(new SkuModel().setSkuId(productModel.getSellerSku()))
                        .setIssues(productModel.getIssues()));
            } else {
                return new PublishTaskExecuteResult(false, null);
            }
        }
    }

    @AllArgsConstructor
    private class ProductPublishCheckCallbackListener implements FutureCallback<PublishTaskExecuteResult> {
        private Long taskId;
        @Override
        public void onSuccess(PublishTaskExecuteResult result) {
            Optional<ErpProductPublishTaskDO> publishTaskOpt = getProductPublishTask(taskId);
            if (!publishTaskOpt.isPresent()) {
                log.warn("[ProductPublishCheckCallbackListener] Task Not Exist, taskId={}", taskId);
                return;
            }
            ErpProductPublishTaskDO publishTask = publishTaskOpt.get();
            ProductListingDO listing = productListingService.getListing(publishTask.getListingId());
            if (listing == null) {
                log.warn("[ProductPublishCheckCallbackListener] Listing Not Exist, ListingId={}", publishTask.getListingId());
                return;
            }
            // 如果失败,
            // 1、check请求失败, 状态未知, 继续下一轮
            // 2、没有错误也没有返回成功的产品ID, 还在进行, 继续等待下一轮check
            if (!result.success || (StringUtils.isEmpty(result.response.getProductId()) && CollectionUtils.isEmpty(result.response.getIssues()))) {
                log.warn("[ProductPublishCheckCallbackListener] Product Creating, TaskId={}, runNext={}", taskId, publishTask.getScheduleTime());
                return;
            }

            boolean isSuccess = false;
            // 只要有ProductId,
            CrossProductPublishStatus status;
            // 有issues
            if (CollectionUtils.isNotEmpty(result.response.getIssues())) {
                status = CrossProductPublishStatus.PUBLISH_FAIL;
                String issueInfo = JsonUtils.toJsonString(result.response.getIssues());
                publishTask.setStatus(status.getStatus());
                publishTask.setRawFeedback(issueInfo);
                publishTask.setBrief(issueInfo);
                publishTask.setStatusInfo(status.getName());
            } else {
                status = CrossProductPublishStatus.PUBLISH_SUC;
                publishTask.setStatus(status.getStatus());
                publishTask.setStatusInfo(status.getName());
            }

            publishTask.setEndTime(LocalDateTime.now());

            updateProductPublishTask(publishTask);
            // 更新 Listing 状态 (10:发布中 -> 99:成功, 91:失败)
            productListingService.updateListingStatus(publishTask.getListingId(), status.getStatus(), taskId, result.response.getProductId());

            // 如果成功, 同步主表
            if (CrossProductPublishStatus.PUBLISH_SUC.equals(status)) {
                crossProductService.syncCrossproductV2(taskId);
            }
        }

        @Override
        public void onFailure(Throwable t) {
            log.warn("[ProductPublishCheckCallbackListener] TaskCheck Process Error, taskId={}", taskId);
        }
    }

}
