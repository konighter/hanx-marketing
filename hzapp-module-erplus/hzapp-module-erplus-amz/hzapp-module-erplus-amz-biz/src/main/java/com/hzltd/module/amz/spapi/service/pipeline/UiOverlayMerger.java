package com.hzltd.module.amz.spapi.service.pipeline;

import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormFieldVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Stage 4: UI Overlay & Grouping.
 * Applies UI-specific metadata (ordering, grouping, widget overrides) to the flattened fields.
 * This stage ensures the final UI matches the "Lingxing" ERP layout.
 */
@Slf4j
@Component
public class UiOverlayMerger {

    private final AmzUiMetadataLoader metadataLoader;

    public UiOverlayMerger(AmzUiMetadataLoader metadataLoader) {
        this.metadataLoader = metadataLoader;
    }

    public void merge(String productType, List<AmzListingFormFieldVO> fields) {
        log.info("[UiOverlayMerger] Merging UI metadata for productType: {}", productType);
        
        for (AmzListingFormFieldVO field : fields) {
            applyMetadata(productType, field);
        }

        // Sort fields by their 'order' property after metadata application
        fields.sort(Comparator.comparing(f -> f.getOrder() != null ? f.getOrder() : 999));
    }

    private void applyMetadata(String productType, AmzListingFormFieldVO field) {
        String id = field.getId();
        
        // 1. Fetch from External Metadata Loader
        AmzFieldMetadata meta = metadataLoader.findMetadata(productType, id);
        
        if (meta != null) {
            if (meta.getGroupName() != null) field.setGroupName(meta.getGroupName());
            if (meta.getOrder() != null) field.setOrder(meta.getOrder());
            if (meta.getUiWidget() != null) field.setUiWidget(meta.getUiWidget());
        } else {
            // Fallback for fields not in metadata config
            field.setGroupName("其他属性");
        }

        // 2. Default Widget Overrides (Legacy fallback)
        if (field.getUiWidget() == null) {
            if (id.contains("description") || id.contains("bullet_point")) {
                field.setUiWidget("textarea");
            } else if (id.contains("date")) {
                field.setUiWidget("date-picker");
            }
        }

        // 3. Process children recursively if composite
        if (field.getChildren() != null) {
            for (AmzListingFormFieldVO child : field.getChildren()) {
                applyMetadata(productType, child);
            }
        }
    }

}
