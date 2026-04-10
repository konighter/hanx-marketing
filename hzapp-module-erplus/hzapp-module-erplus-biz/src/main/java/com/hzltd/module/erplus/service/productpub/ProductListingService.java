package com.hzltd.module.erplus.service.productpub;

import com.hzltd.module.erplus.dal.dataobject.productpub.ProductListingDO;

import java.util.Collection;
import java.util.Map;

/**
 * 跨境商品刊登状态 Service 接口
 *
 * @author Antigravity
 */
public interface ProductListingService {

    /**
     * 创建或更新刊登状态
     *
     * @param productId 商品 ID
     * @param syncStatus 状态
     * @param taskId 关联任务 ID
     */
    void saveOrUpdateListingStatus(Long productId, Integer syncStatus, Long taskId);

    /**
     * 获取刊登状态
     *
     * @param productId 商品 ID
     * @return 刊登状态
     */
    ProductListingDO getListingStatusByProductId(Long productId);

    /**
     * 批量获取刊登状态
     *
     * @param productIds 商品 ID 集合
     * @return 刊登状态 Map (productId -> ProductListingDO)
     */
    Map<Long, ProductListingDO> getListingStatusMap(Collection<Long> productIds);

    /**
     * 获取或创建刊登记录 (根据 SKU 维度)
     */
    ProductListingDO getOrCreateListing(Integer platformId, Integer shopId, String marketId, String sellerSku);

    /**
     * 更新刊登状态 (根据记录 ID)
     *
     * @param id 记录 ID
     * @param syncStatus 状态
     * @param taskId 关联任务 ID
     */
    void updateListingStatus(Long id, Integer syncStatus, Long taskId, String productId);

    /**
     * 更新刊登状态 (根据记录 ID)
     *
     * @param id 记录 ID
     * @param taskId 关联任务 ID
     */
    void saveOrUpdateListingStatus(Long id, Long taskId);

    /**
     * 根据 ID 获取刊登记录
     */
    ProductListingDO getListing(Long id);

    /**
     * 更新刊登记录
     */
    void updateListing(ProductListingDO listing);
}
