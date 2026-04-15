package com.hzltd.module.amz.spapi.service.pipeline;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * UI Metadata for a specific Amazon field.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmzFieldMetadata {
    private String groupName;
    private Integer order;
    private String uiWidget;
    private Integer span;
    private String label;
    private String tooltip;
    private String placeholder;
    private Boolean system;
}
