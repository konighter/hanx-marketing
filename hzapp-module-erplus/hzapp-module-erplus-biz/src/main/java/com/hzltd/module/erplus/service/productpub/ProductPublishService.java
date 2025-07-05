package com.hzltd.module.erplus.service.productpub;

import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishResponse;

public interface ProductPublishService {


     ProductPublishResponse publishProduct(ProductPublishRequest request);
}
