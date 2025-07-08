package com.hzltd.module.erplus.service.productpub;

import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishResponse;

public interface ProductPublishService {

     /**
      * 发布商品
      * @param request
      * @return
      */
     ProductPublishResponse publishProduct(ProductPublishRequest request);

     /**
      * 提交发布任务
      * @param taskId
      * @return
      */
     boolean submitProductPublishTask(Long taskId);
}
