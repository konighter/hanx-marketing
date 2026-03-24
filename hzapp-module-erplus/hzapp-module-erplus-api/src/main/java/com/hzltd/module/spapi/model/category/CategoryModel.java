package com.hzltd.module.spapi.model.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CategoryModel {

    private String categoryId;

    private String name;

    private String parentCategoryId;

    private boolean isLeaf;

    private String extra;
}
