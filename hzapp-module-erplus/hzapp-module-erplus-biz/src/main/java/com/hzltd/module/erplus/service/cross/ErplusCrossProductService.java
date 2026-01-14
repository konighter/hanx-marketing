package com.hzltd.module.erplus.service.cross;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.controller.admin.cross.vo.*;
import com.hzltd.module.erplus.controller.admin.productpub.vo.ProductPublishRequest;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.service.productpub.vo.CrossPlatformProductVO;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Optional;

/**
 * 跨境商品服务
 * 包括跨境商品的查询，状态管理、价格管理、关系管理等操作
 * sku维度操作
 */
public interface ErplusCrossProductService {


    /**
     * 同步跨境商品
     * @param request
     * @return
     */
    @Async
    void syncProductListing(CrossProductSyncRequest request);
    /**
     * 合并跨境商品属性(亚马逊 or Ozeon)
     * @param request
     * @return
     */
    Boolean mergeCrossProductVariation(CrossProductVariationMergeRequest request);

    /**
     * 添加跨境商品属性(支持SPU-SKU结构的平台)
     * @param request
     * @return
     */
    Boolean addCrossProductVariation(CrossProductVariationAddRequest request);

    /**
     * 查询跨境商品
     * @param request
     * @return
     */
    PageResult<?> getCrossPlatformProductPage(CrossProductPageRequest request);

    /**
     * 处理跨境商品状态变更事件
     * @param event
     */
    void productChangeEventAction(ProductChangeEvent event);


    Optional<CrossPlatformProductVO> getCrossPlatformProduct(Long productId);

    Optional<CrossProductDO> getBasicCrossPlatformProduct(Long productId);

    Optional<List<CrossProductDO>> getBasicCrossPlatformProduct(List<Long> productIds);

    Optional<CrossProductDO> getBasicCrossPlatformProductByPlatformIdAndProductId(Integer platformId, String sellerSku, String platformProductCode);

    List<CrossProductDO> getBasicCrossProductBySkus(Integer platformId, Integer shopId, String marketId, List<String> skus);

    Long saveCrossPlatformProduct(ProductPublishRequest request);

    Boolean validCrossPlatformProduct(Long productId);
}
