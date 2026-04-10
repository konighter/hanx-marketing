package com.hzltd.module.erplus.system.model;

import lombok.Data;

@Data
public class CrossMetaCategoryModel {
    /**
     * ID
     */
    private Integer id;
    /**
     * 品类标识
     */
    private String categoryCode;
    /**
     * 品类名
     */
    private String categoryName;
    /**
     * 父品类
     */
    private String parentCategoryCode;
    /**
     * 层级
     */
    private Integer level;
    /**
     * 是否叶子节点
     */
    private Boolean leaf;
    /**
     * 平台ID
     */
    private Integer platformId;
    /**
     * 扩展字段 (JSON Schema)
     */
    private String extra;
}
