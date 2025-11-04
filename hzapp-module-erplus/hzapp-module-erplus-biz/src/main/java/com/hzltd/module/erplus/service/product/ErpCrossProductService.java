package com.hzltd.module.erplus.service.product;

import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.dal.dataobject.product.ErpCrossProductDO;
import com.hzltd.module.erplus.service.productpub.vo.CrossPlatformProductVO;

import java.util.Optional;

public interface ErpCrossProductService {

    Optional<CrossPlatformProductVO> getCrossPlatformProduct(Long productId);

    Optional<ErpCrossProductDO> getBasicCrossPlatformProduct(Long productId);

    Long saveCrossPlatformProduct(ProductPublishRequest request);

    Boolean validCrossPlatformProduct(Long productId);
}
