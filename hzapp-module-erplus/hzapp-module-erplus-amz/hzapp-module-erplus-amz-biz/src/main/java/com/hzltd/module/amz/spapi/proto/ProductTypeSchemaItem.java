package com.hzltd.module.amz.spapi.proto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductTypeSchemaItem {
    private String title;
    private String description;
    private Boolean editable;
    private Boolean hidden;
    private List<String> examples;
    private String type;

    /**
     * 如何是object类型, properties必填的字段
     */
    private List<String> required;
    @JsonProperty("enum")
    private List<String> enumValues;

    private List<String> enumNames;

    private List<String> selectors;

    private ProductTypeSchemaItem items;

    private Map<String, ProductTypeSchemaItem> properties;

    private Integer minItems;

    private Integer minUniqueItems;

    private Integer maxUniqueItems;
}
