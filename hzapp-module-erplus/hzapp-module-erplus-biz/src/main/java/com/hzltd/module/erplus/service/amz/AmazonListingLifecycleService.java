package com.hzltd.module.erplus.service.amz;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.spapi.validator.ListingSchemaValidator;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryAttributeDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductAttrsDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossProductDO;
import com.hzltd.module.erplus.dal.mysql.category.CrossMetaCategoryAttributeMapper;
import com.hzltd.module.erplus.dal.mysql.cross.CrossProductMapper;
import com.hzltd.module.erplus.dal.mysql.cross.ErpCrossProductAttrsMapper;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service for managing Amazon Listing Lifecycle (CRUD + Sync).
 * Implements the hybrid storage strategy: full JSON in 'extra' + flattened KV in 'attrs' table.
 */
@Slf4j
@Service
public class AmazonListingLifecycleService {

    @Resource
    private CrossProductMapper crossProductMapper;

    @Resource
    private ErpCrossProductAttrsMapper crossProductAttrsMapper;

    @Resource
    private CrossMetaCategoryAttributeMapper metaAttributeMapper;

    @Resource
    private ListingSchemaValidator listingValidator;

    /**
     * Saves a listing to the local database after validation.
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveListingLocally(Integer shopId, String marketplaceId, String sku, String productType, Map<String, Object> attributes) {
        log.info("[saveListingLocally] Saving listing: shopId={}, sku={}, productType={}", shopId, sku, productType);

        // 1. Validate against stored schema
        CrossMetaCategoryAttributeDO rootAttr = metaAttributeMapper.selectOne(new LambdaQueryWrapper<CrossMetaCategoryAttributeDO>()
                .eq(CrossMetaCategoryAttributeDO::getPlatformId, CrossPlatformEnum.AMAZON.getValue())
                .eq(CrossMetaCategoryAttributeDO::getCategoryCode, productType)
                .eq(CrossMetaCategoryAttributeDO::getAttrCode, "$root"));

        if (rootAttr != null && rootAttr.getExtra() != null) {
            String schemaJson = rootAttr.getExtra();
            // Amazon schema usually defines the attributes directly at the root level.
            // So we validate the 'attributes' map directly.
            String dataJson = JsonUtils.toJsonString(attributes);
            List<String> errors = listingValidator.validate(schemaJson, dataJson);
            if (!errors.isEmpty()) {
                log.warn("[saveListingLocally] Validation failed for SKU {}: {}", sku, errors);
                // Throwing validation exception in the future would be better here.
            }
        }

        // 2. Find or Create CrossProductDO
        CrossProductDO product = crossProductMapper.selectOne(new LambdaQueryWrapper<CrossProductDO>()
                .eq(CrossProductDO::getShopId, shopId)
                .eq(CrossProductDO::getSellerSkuCode, sku)
                .eq(CrossProductDO::getMarketId, marketplaceId));

        if (product == null) {
            product = new CrossProductDO();
            product.setPlatformId(CrossPlatformEnum.AMAZON.getValue());
            product.setShopId(shopId);
            product.setMarketId(marketplaceId);
            product.setSellerSkuCode(sku);
            crossProductMapper.insert(product);
        }

        product.setCategoryId(productType);
        product.setExtra(JsonUtils.toJsonString(attributes)); // Hybrid Storage: Full JSON Blob
        
        syncCommonFields(product, attributes);

        syncCommonFields(product, attributes);

        crossProductMapper.updateById(product);

        // 3. Persist flattened attributes (truncate values for legacy compatibility)
        persistFlattenedAttributes(product.getId(), attributes);
    }

    private void syncCommonFields(CrossProductDO product, Map<String, Object> attributes) {
        if (attributes.containsKey("item_name")) {
            product.setTitle(extractValue(attributes.get("item_name")));
        }
        if (attributes.containsKey("brand")) {
            product.setBrand(extractValue(attributes.get("brand")));
        }
    }

    private String extractValue(Object attrObj) {
        if (attrObj instanceof List) {
            List<?> list = (List<?>) attrObj;
            if (!list.isEmpty()) {
                Object first = list.get(0);
                if (first instanceof Map) {
                    return String.valueOf(((Map<?, ?>) first).get("value"));
                }
            }
        }
        return String.valueOf(attrObj);
    }

    private void persistFlattenedAttributes(Long productId, Map<String, Object> attributes) {
        crossProductAttrsMapper.delete(new LambdaQueryWrapper<CrossProductAttrsDO>()
                .eq(CrossProductAttrsDO::getProductId, productId));

        List<CrossProductAttrsDO> attrsToSave = new ArrayList<>();
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // Extract a reader-friendly string for the attributes table
            String displayValue = extractValue(value);
            
            // If it's still complex (e.g. JSON string), truncate it reasonably
            if (displayValue.length() > 255) {
                displayValue = displayValue.substring(0, 252) + "...";
            }

            attrsToSave.add(CrossProductAttrsDO.builder()
                    .productId(productId)
                    .attrId(key)
                    .attrName(key)
                    .attrValue(displayValue)
                    .build());
        }

        if (!attrsToSave.isEmpty()) {
            attrsToSave.forEach(crossProductAttrsMapper::insert);
        }
    }
}
