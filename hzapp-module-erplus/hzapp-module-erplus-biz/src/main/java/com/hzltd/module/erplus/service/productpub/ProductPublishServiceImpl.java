package com.hzltd.module.erplus.service.productpub;

import com.google.common.collect.Lists;
import com.hzltd.framework.common.util.object.ObjectUtils;
import com.hzltd.module.erpls.api.model.product.CreateProductRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishResponse;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishTaskVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductPublishServiceImpl implements ProductPublishService {

    public ProductPublishResponse publishProduct(ProductPublishRequest request) {
        // 拆分单平台单店铺商品
        List<ProductPublishRequest> productPublishRequests = request.getShopIds().stream()
                .map(id -> ObjectUtils.cloneIgnoreId(request, r -> r.setShopIds(Lists.newArrayList(id))))
                .collect(Collectors.toList());

        // 保存平台商品信息
        List<Long> productIds = productPublishRequests.stream().map(this::saveCrossPlatformProduct).collect(Collectors.toList());

        // 生成并提交发布任务
        List<ProductPublishTaskVO> productPublishTasks = productIds.stream().map(this::saveProductPublishTask).collect(Collectors.toList());

        // 返回任务状态
        ProductPublishResponse response = new ProductPublishResponse();
        response.setProductPublishTasks(productPublishTasks);

        return response;
    }

    private Long saveCrossPlatformProduct(ProductPublishRequest request) {
        // 查询店铺授权信息
        // 校验本地商品和SKU(有效/创建)
        CreateProductRequest product = this.buildProductDetail(request);

        // 创建CreateProductRequest:
        Long productId = this.doSaveCrossPlatformProduct(product);

        // 设置同步状态和商品状态


        return null;
    }


    private CreateProductRequest buildProductDetail(ProductPublishRequest request) {
        // 查询品牌关联信息


        return null;
    }

    private Long doSaveCrossPlatformProduct(CreateProductRequest product) {
        return null;
    }


    private ProductPublishTaskVO saveProductPublishTask(Long productId) {
        // 查询发布任务 没有则创建

        // 如果非定时发送, 提交到任务队列。

        // 返回发布任务详情
        return null;
    }


}
