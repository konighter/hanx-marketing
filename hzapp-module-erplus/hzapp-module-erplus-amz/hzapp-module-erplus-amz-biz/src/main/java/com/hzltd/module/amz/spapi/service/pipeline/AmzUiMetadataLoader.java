package com.hzltd.module.amz.spapi.service.pipeline;

import cn.hutool.core.io.resource.ResourceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Loads Amazon UI metadata from a JSON resource file.
 */
@Slf4j
@Component
public class AmzUiMetadataLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private AmzUiMetadataConfig config;

    @PostConstruct
    public void init() {
        try {
            log.info("[AmzUiMetadataLoader] Loading UI metadata from classpath:metadata/amz-ui-metadata.json");
            String json = ResourceUtil.readStr("metadata/amz-ui-metadata.json", StandardCharsets.UTF_8);
            this.config = objectMapper.readValue(json, AmzUiMetadataConfig.class);
            log.info("[AmzUiMetadataLoader] Loaded {} common field presets", 
                config.getCommon() != null ? config.getCommon().size() : 0);
        } catch (Exception e) {
            log.error("[AmzUiMetadataLoader] Failed to load UI metadata", e);
            this.config = new AmzUiMetadataConfig(); // Fallback to empty
        }
    }

    /**
     * Finds metadata for a field, favoring type-specific overrides over common settings.
     */
    public AmzFieldMetadata findMetadata(String productType, String fieldId) {
        if (config == null) return null;

        // 1. Try Type-Specific Override
        if (productType != null && config.getTypeSpecific() != null) {
            Map<String, AmzFieldMetadata> typeMap = config.getTypeSpecific().get(productType);
            if (typeMap != null) {
                AmzFieldMetadata meta = matchPattern(typeMap, fieldId);
                if (meta != null) return meta;
            }
        }

        // 2. Try Common Global Settings
        if (config.getCommon() != null) {
            return matchPattern(config.getCommon(), fieldId);
        }

        return null;
    }

    private AmzFieldMetadata matchPattern(Map<String, AmzFieldMetadata> map, String fieldId) {
        // Precise match
        if (map.containsKey(fieldId)) return map.get(fieldId);

        // Pattern match (startsWith) for nested properties
        for (Map.Entry<String, AmzFieldMetadata> entry : map.entrySet()) {
            if (fieldId.startsWith(entry.getKey() + ".")) {
                return entry.getValue();
            }
        }
        
        return null;
    }
}
