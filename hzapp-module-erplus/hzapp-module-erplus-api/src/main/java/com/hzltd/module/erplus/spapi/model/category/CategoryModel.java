package com.hzltd.module.erplus.spapi.model.category;

import lombok.Data;

@Data
public class CategoryModel {

    private String categoryId;

    private String name;

    private String parentCategoryId;

    private boolean isLeaf;

    private String extra;
}
