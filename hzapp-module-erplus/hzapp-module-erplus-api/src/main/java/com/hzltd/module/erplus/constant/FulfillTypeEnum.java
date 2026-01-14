package com.hzltd.module.erplus.constant;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FulfillTypeEnum implements IntArrayValuable {
    /**
     * 商家自配送
     */
    FBM(1, "商家自配送"),
    /**
     * 平台配送
     */
    FBA(2, "平台配送"),
    /**
     * 合作伙伴配送
     */
    FBP(3, "合作伙伴配送"),

    /**
     * 全托管模式
     */
    FMA(4, "全托管模式"),

    /**
     * 全托管模式
     */
    FMO(5, "半托管模式"),

    UNKNOWN(99, "未知");

    private final Integer code;
    private final String name;



    @Override
    public int[] array() {
        return new int[0];
    }

    public static FulfillTypeEnum of(Integer code) {
        for (FulfillTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return UNKNOWN;
    }
}
