package com.hzltd.module.erplus.spapi.model.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Result of parsing a platform category schema.
 * Contains both the high-level attributes and the raw JSON schema.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaCategorySchemaResult {

    /**
     * Parsed attribute models for standard ERP display/filtering.
     */
    private List<CategoryAttributeModel> attributes;

    /**
     * The full raw JSON schema for dynamic form logic and validation.
     */
    private String fullSchema;

}
