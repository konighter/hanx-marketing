package com.hzltd.module.erplus.spapi.enums;

import com.hzltd.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 商品 SPU 规格类型
 */
@Getter
@AllArgsConstructor
public enum ProductSpuSpecTypeEnum implements ArrayValuable<Integer> {

    SINGLE(1, "单规格"),
    MULTI(2, "多规格");

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(ProductSpuSpecTypeEnum::getType).toArray(Integer[]::new);

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
