package com.hzltd.module.spapi.enums;

import cn.hutool.core.util.ArrayUtil;
import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum AttributeTypeEnum implements IntArrayValuable {

    SALES_PROPERTY(0, "销售属性"),  // 销售属性, 用于变体
    PRODUCT_PROPERTY(1, "商品属性") // 商品属性, 用户商品描述
    ;


    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(AttributeTypeEnum::getValue).toArray();
    private Integer value;
    private String name;


    public static AttributeTypeEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(t -> t.getValue().equals(value), AttributeTypeEnum.values());
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }

}
