package com.hzltd.module.erplus.service.productpub;

import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishResponse;

public interface ProductPublishService {

     /**
      * 发布商品
      * @param request
      * @return
      */
     ProductPublishResponse publishProduct(ProductPublishRequest request, Long operatorId);

    /**
     * 刊登商品 (V2 SKU维度)
     *
     * @param request 请求
     * @param operatorId 操作人 ID
     * @return 响应
     */
    ProductPublishResponse publishProductV2(com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishV2Request request, Long operatorId);

}
