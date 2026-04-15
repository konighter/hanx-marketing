package com.hzltd.module.amz.spapi.service.pipeline;

import lombok.Data;
import java.util.Map;

/**
 * Root configuration model for AMZ UI metadata.
 */
@Data
public class AmzUiMetadataConfig {
    /**
     * Common field definitions applied across all product types.
     * Key: Field ID or prefix.
     */
    private Map<String, AmzFieldMetadata> common;

    /**
     * Type-specific overrides.
     * Key: productType (e.g., 3D_PRINTER).
     */
    private Map<String, Map<String, AmzFieldMetadata>> typeSpecific;
}
