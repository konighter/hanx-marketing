package com.hzltd.module.erplus.model.category;

import lombok.Data;

@Data
public class CategoryModel {

    private String categoryId;

    private String name;

    private String parentCategoryId;

    private boolean isLeaf;
}
