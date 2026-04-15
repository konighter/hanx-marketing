package com.hzltd.module.amz.spapi.service.pipeline;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.CrossMetaCategoryModel;
import com.hzltd.module.erplus.system.service.SystemMetaCategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Stage 1: Responsible for loading Amazon JSON Schemas from the database and caching them in memory.
 * This stage ensures we don't repeatedly parse large JSON schemas (200KB+) during form generation.
 */
@Slf4j
@Service
public class ListingSchemaLoader {

    @Resource
    private SystemMetaCategoryService metaCategoryService;

    // LRU Cache: productType -> parsed JsonNode
    private final Cache<String, JsonNode> schemaCache = Caffeine.newBuilder()
            .maximumSize(64)
            .expireAfterWrite(Duration.ofHours(6))
            .build();

    /**
     * Loads the schema for a specific product type, using the cache if available.
     */
    public JsonNode loadSchema(String productType) {
        return schemaCache.get(productType, pt -> {
            log.info("[ListingSchemaLoader] Cache miss for productType: {}, fetching from DB", pt);
            CrossMetaCategoryModel crossMetaCategory = metaCategoryService.getCrossMetaCategoryByPlatformCategoryCode(
                    CrossPlatformEnum.AMAZON, pt);

            if (crossMetaCategory == null || StrUtil.isEmpty(crossMetaCategory.getExtra())) {
                log.warn("[ListingSchemaLoader] No schema found for productType: {}", pt);
                return null;
            }

            try {
                return JsonUtils.parseTree(crossMetaCategory.getExtra());
            } catch (Exception e) {
                log.error("[ListingSchemaLoader] Failed to parse JSON schema for productType: {}", pt, e);
                return null;
            }
        });
    }

    /**
     * Clears the cache for a specific product type (useful for schema updates).
     */
    public void invalidate(String productType) {
        schemaCache.invalidate(productType);
    }
}
