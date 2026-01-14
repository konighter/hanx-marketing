package com.hzltd.module.erplus.constant;

import com.hzltd.framework.common.core.IntArrayValuable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 入库方式
 *   {value: 0, label: '采购入库'},
 *   {value: 1, label: '调拨入库'},
 *   {value: 2, label: '其他入库'},
 */
@Getter
@AllArgsConstructor
public enum InboundSrcTypeEnum implements IntArrayValuable {

    PURCHASE(0, ""),

    TRANSFER(2, "调拨入库"),

    OTHER(2, "其他入库");

    private final Integer code;

    private final String name;


    @Override
    public int[] array() {
        return new int[0];
    }

    public static InboundSrcTypeEnum of(Integer code) {
        for (InboundSrcTypeEnum value : values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return OTHER;
    }
}
