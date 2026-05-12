package com.hzltd.module.amz.spapi.service.pipeline;

import com.hzltd.module.amz.spapi.controller.admin.listing.vo.AmzListingFormFieldVO;
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
        
        // 1. Filter out system fields (Title, Brand, Bullets, etc.)
        Iterator<AmzListingFormFieldVO> iterator = fields.iterator();
        while (iterator.hasNext()) {
            AmzListingFormFieldVO field = iterator.next();
            if (isSystemField(productType, field)) {
                log.debug("[UiOverlayMerger] Removing system field from dynamic form: {}", field.getId());
                iterator.remove();
                continue;
            }
            // Apply metadata and process children
            applyMetadata(productType, field);
        }

        // 2. Sort remaining fields
        fields.sort(Comparator.comparing(f -> f.getOrder() != null ? f.getOrder() : 999));
    }

    private boolean isSystemField(String productType, AmzListingFormFieldVO field) {
        AmzFieldMetadata meta = metadataLoader.findMetadata(productType, field.getId());
        return meta != null && Boolean.TRUE.equals(meta.getSystem());
    }

    private void applyMetadata(String productType, AmzListingFormFieldVO field) {
        String id = field.getId();
        
        // 1. Fetch from External Metadata Loader
        AmzFieldMetadata meta = metadataLoader.findMetadata(productType, id);
        
        if (meta != null) {
            if (meta.getGroupName() != null) field.setGroupName(meta.getGroupName());
            if (meta.getOrder() != null) field.setOrder(meta.getOrder());
            if (meta.getUiWidget() != null) field.setUiWidget(meta.getUiWidget());
        }
        
        // 2. Default Grouping if still null
        if (field.getGroupName() == null) {
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
            Iterator<AmzListingFormFieldVO> childIterator = field.getChildren().iterator();
            while (childIterator.hasNext()) {
                AmzListingFormFieldVO child = childIterator.next();
                if (isSystemField(productType, child)) {
                    childIterator.remove();
                    continue;
                }
                applyMetadata(productType, child);
            }
        }
    }

}
