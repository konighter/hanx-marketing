package com.hzltd.module.erplus.api.amz.proto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductTypeSchema {
    private List<String> required;
    private Map<String, ProductTypeSchemaItem> properties;
}
