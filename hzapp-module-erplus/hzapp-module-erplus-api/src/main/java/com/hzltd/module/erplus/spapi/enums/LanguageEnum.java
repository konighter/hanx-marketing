package com.hzltd.module.erplus.spapi.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LanguageEnum implements IntArrayValuable {
    ZH_CN(1, "zh_cn"),
    ;

    private final Integer code;
    private final String name;


    @Override
    public int[] array() {
        return new int[0];
    }
}
