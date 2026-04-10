package com.hzltd.module.erplus.service.productpub;

import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.dal.dataobject.productpub.ProductListingDO;
import com.hzltd.module.erplus.dal.mysql.productpub.ProductListingMapper;
import com.hzltd.module.erplus.spapi.enums.CrossProductPublishStatus;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 跨境商品刊登状态 Service 实现类
 *
 * @author Antigravity
 */
@Service
@Validated
public class ProductListingServiceImpl implements ProductListingService {

    @Resource
    private ProductListingMapper productListingMapper;

    @Override
    public void saveOrUpdateListingStatus(Long productId, Integer syncStatus, Long taskId) {
        ProductListingDO listing = productListingMapper.selectByProductId(productId);
        if (listing == null) {
            listing = ProductListingDO.builder()
                    .productId(productId)
                    .syncStatus(syncStatus)
                    .latestTaskId(taskId)
                    .publishTime(LocalDateTime.now())
                    .build();
            productListingMapper.insert(listing);
        } else {
            listing.setSyncStatus(syncStatus);
            if (taskId != null) {
                listing.setLatestTaskId(taskId);
            }
            listing.setPublishTime(LocalDateTime.now());
            productListingMapper.updateById(listing);
        }
    }

    @Override
    public ProductListingDO getOrCreateListing(Integer platformId, Integer shopId, String marketId, String sellerSku) {
        ProductListingDO listing = productListingMapper.selectBySku(platformId, shopId, marketId, sellerSku);
        if (listing == null) {
            listing = ProductListingDO.builder()
                    .platformId(platformId)
                    .shopId(shopId)
                    .marketId(marketId)
                    .sellerSku(sellerSku)
                    .syncStatus(CrossProductPublishStatus.INIT.getStatus()) // INIT
                    .build();
            productListingMapper.insert(listing);
        }
        return listing;
    }

    @Override
    public void updateListingStatus(Long id, Integer syncStatus, Long taskId, String platformProductCode) {
        ProductListingDO listing = productListingMapper.selectById(id);
        if (listing != null) {
            listing.setSyncStatus(syncStatus);
            if (taskId != null) {
                listing.setLatestTaskId(taskId);
            }
            if (StringUtils.isNotEmpty(platformProductCode)) {
                listing.setPlatformProductCode(platformProductCode);
            }
            listing.setPublishTime(LocalDateTime.now());
            productListingMapper.updateById(listing);
        }
    }

    @Override
    public void saveOrUpdateListingStatus(Long id, Long taskId) {
        this.updateListingStatus(id, 10, taskId, null); // 10: AUDITING/WAITING
    }

    @Override
    public ProductListingDO getListingStatusByProductId(Long productId) {
        return productListingMapper.selectByProductId(productId);
    }

    @Override
    public ProductListingDO getListing(Long id) {
        return productListingMapper.selectById(id);
    }

    @Override
    public void updateListing(ProductListingDO listing) {
        productListingMapper.updateById(listing);
    }

    @Override
    public Map<Long, ProductListingDO> getListingStatusMap(Collection<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return productListingMapper.selectList(new LambdaQueryWrapperX<ProductListingDO>()
                .in(ProductListingDO::getProductId, productIds))
                .stream()
                .collect(Collectors.toMap(ProductListingDO::getProductId, l -> l));
    }
}
