package com.hzltd.module.erplus.spapi.enums;

import com.hzltd.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 商品 SKU 产品类型
 */
@Getter
@AllArgsConstructor
public enum ProductSkuTypeEnum implements ArrayValuable<Integer> {

    ORDINARY(1, "普通产品"),
    COMBO(2, "组合产品");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ProductSkuTypeEnum::getType).toArray(Integer[]::new);

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 类型名
     */
    private final String name;

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
