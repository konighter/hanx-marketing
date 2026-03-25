package com.hzltd.module.erplus.service.productpub;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.hzltd.framework.common.exception.ServiceException;
import com.hzltd.framework.common.util.object.ObjectUtils;
import com.hzltd.module.erplus.api.service.ProductApiFactory;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishResponse;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishTaskVO;
import com.hzltd.module.erplus.controller.admin.productpub.vo.SkuVO;
import com.hzltd.module.erplus.convert.spu.ProductSpuConvert;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.dal.dataobject.productpub.ErpProductPublishTaskDO;
import com.hzltd.module.spapi.enums.CrossProductPublishStatus;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.ApiRequest;
import com.hzltd.module.spapi.model.ApiResponse;
import com.hzltd.module.spapi.model.product.CreateProductRequest;
import com.hzltd.module.spapi.model.product.CreateProductResponse;
import com.hzltd.module.erplus.service.executor.ExecutorService;
import com.hzltd.module.erplus.service.cross.ErplusCrossProductService;
import com.hzltd.module.spapi.service.product.ProductApi;
import com.hzltd.module.erplus.service.spu.ProductSpuService;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.PRODUCT_NOT_EXISTS;

@Slf4j
@Service
public class ProductPublishServiceImpl implements ProductPublishService {

    @Resource
    private ProductSpuService productSpuService;

    @Resource
    private ErplusCrossProductService crossProductService;

//    @Resource
    private ErpProductPublishTaskService productPublishTaskService;

    @Resource
    private ProductApiFactory productApiFactory;



    public ProductPublishResponse publishProduct(ProductPublishRequest request) {
        // 拆分单平台单店铺商品
        List<ProductPublishRequest> productPublishRequests = request.getShopIds().stream()
                .map(id -> ObjectUtils.cloneIgnoreId(request, r -> r.setShopIds(Lists.newArrayList(id))))
                .collect(Collectors.toList());

        // 保存平台商品信息
        List<Long> productIds = productPublishRequests.stream().map(this::saveCrossPlatformProduct).toList();

        // 生成并提交发布任务
        List<ProductPublishTaskVO> productPublishTasks = productIds.stream().map(id -> {
            return this.createProductPublishTask(id, request.getScheduleTime());
        }).collect(Collectors.toList());

        // 返回任务状态
        ProductPublishResponse response = new ProductPublishResponse();
        response.setProductPublishTasks(productPublishTasks);

        return response;
    }

    private Long saveCrossPlatformProduct(ProductPublishRequest request) {
        // 如果没有关联了本地商品则创建一个本地商品用于后续采购和本地库存
        if (request.getRelatedProductId() == null) {
            Long spuId = productSpuService.createSpu(ProductSpuConvert.INSTANCE.convert(request));
            request.setRelatedProductId(spuId);
        }

        // 创建CreateProductRequest:
        Long productId = this.doCreateCrossPlatformProduct(request);

        this.doCreateCrossProductSku(productId, request.getSkus());
        return productId;
    }





    private Long doCreateCrossPlatformProduct(ProductPublishRequest request) {


        return crossProductService.saveCrossPlatformProduct(request);
    }

    private void doCreateCrossProductSku(Long crossProductId, List<SkuVO> skus) {

    }


    private ProductPublishTaskVO createProductPublishTask(Long productId, DateTime scheduleTime) {
        Optional<CrossProductDO> crossProductOption = crossProductService.getBasicCrossPlatformProduct(productId);
        if (!crossProductOption.isPresent()) {
            throw new ServiceException(PRODUCT_NOT_EXISTS);
        }

        CrossProductDO crossProduct = crossProductOption.get();

        Optional<ErpProductPublishTaskDO> taskOptional = productPublishTaskService.getProductPublishTask(crossProduct.getId(),crossProduct.getVersion());
        if (taskOptional.isPresent()) {
            log.warn("Product[id:{}, version:{}] have a task already", crossProduct.getId(), crossProduct.getVersion());
            return new ProductPublishTaskVO().setProductId(taskOptional.get().getProductId()).setTaskId(taskOptional.get().getId()).setStatus(CrossProductPublishStatus.of(taskOptional.get().getStatus()));
        }

        ErpProductPublishTaskDO taskDO = ErpProductPublishTaskDO.builder()
                .productId(crossProduct.getId())
                .platformId(crossProduct.getPlatformId())
                .scheduleTime(scheduleTime)
                .version(crossProduct.getVersion())
                .status(CrossProductPublishStatus.INIT.getStatus())
                .build();

        Long taskId = productPublishTaskService.createProductPublishTask(taskDO);

        // 立即提交
        if (scheduleTime == null) {
            submitProductPublishTask(taskId);
        }

        return new ProductPublishTaskVO().setTaskId(taskId).setProductId(taskDO.getProductId()).setStatus(CrossProductPublishStatus.of(taskDO.getStatus()));
    }

    @Override
    public boolean submitProductPublishTask(Long taskId) {
        ListenableFuture<PublishTaskExecuteResult> future = ExecutorService.submitProductPublishTask(new ProductPublishTask(taskId));
        Futures.addCallback(future, new ProductPublishCallbackListener(taskId), ExecutorService.getCallbackExecutorService());
        // 更新任务为已提交
        return true;
    }

    @AllArgsConstructor
    public static class PublishTaskExecuteResult {
        ApiResponse<CreateProductResponse> response;

    }

    private CreateProductRequest buildCrossCreateProductRequest(Long productId) {



        return null;
    }



    @AllArgsConstructor
    public class ProductPublishTask implements Callable<PublishTaskExecuteResult> {

        private Long taskId;

        @Override
        public PublishTaskExecuteResult call() throws Exception {
            Optional<ErpProductPublishTaskDO> publishTask = productPublishTaskService.getProductPublishTask(taskId);
            if (!publishTask.isPresent()) {
                throw new ServiceException(PRODUCT_NOT_EXISTS);
            }
            Optional<CrossProductDO> crossProduct = crossProductService.getBasicCrossPlatformProduct(publishTask.get().getProductId());
            if (!crossProduct.isPresent()) {
                throw new ServiceException(PRODUCT_NOT_EXISTS);
            }

            ProductApi crossServiceCompositeApi = productApiFactory.getCrossApiService(CrossPlatformEnum.AMAZON);

            ApiResponse<CreateProductResponse> response = crossServiceCompositeApi.createProduct(new ApiRequest<CreateProductRequest>().setRequest(buildCrossCreateProductRequest(crossProduct.get().getId())));
            // 更新任务为提交成功或者失败

            return new PublishTaskExecuteResult(response);
        }
    }

    @AllArgsConstructor
    private static class ProductPublishCallbackListener implements FutureCallback<PublishTaskExecuteResult> {

        private Long taskId;

        @Override
        public void onSuccess(PublishTaskExecuteResult result) {
            //todo-- 通知卖家同步结果
            log.info("ProductPublishSuccess: {}", result);
        }

        @Override
        public void onFailure(@NotNull Throwable t) {
            log.error("ProductPublishError: {}", this.taskId, t);
        }
    }





}
