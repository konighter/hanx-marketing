package com.hzltd.module.erplus.spapi.enums;

import com.hzltd.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 商品 SPU 状态
 * | 状态 | 含义 | 可操作 |
 * |------|------|--------|
 * | `DRAFT` | 草稿，信息不完整或未审核 | 编辑、提交审核 |
 * | `APPROVED` | 已批准，可用于刊登 | 选品、查看、停售 |
 * | `REJECTED` | 审核拒绝（如缺品牌、类目错） | 查看原因、重新编辑 |
 * | `ARCHIVED` | 已归档（不再铺货） | 仅查看、可重新启用 |
 *
 * @author 翰展科技
 */
@Getter
@AllArgsConstructor
public enum ProductSpuStatusEnum implements ArrayValuable<Integer> {

    DRAFT(0, "草稿"),

    FOR_SALE(1, "上架"),
    OFF_SALE(2, "下架"),
    APPROVED(3, "已批准"),
    REJECTED(4, "审核拒绝"),
    ARCHIVED(-1, "已归档"),
//    ARCHIVED(-1, "已归档");

    ;
    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ProductSpuStatusEnum::getStatus).toArray(Integer[]::new);

    /**
     * 状态
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

    /**
     * 判断是否处于【上架】状态
     *
     * @param status 状态
     * @return 是否处于【上架】状态
     */
    public static boolean isEnable(Integer status) {
        return FOR_SALE.getStatus().equals(status);
    }

}
