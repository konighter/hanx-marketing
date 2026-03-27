package com.hzltd.module.system.enums;

import cn.hutool.core.util.ArrayUtil;
import com.hzltd.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


@Getter
@AllArgsConstructor
public enum CrossPlatformEnum implements ArrayValuable<Integer> {
    AMAZON(3, "Amazon","亚马逊"),
    OZON(5, "Ozon","Ozon"),
    TTS(4, "TTS","Tiktok Shop"),
    ALIEXPRESS(6, "ALIEXPRESS","速卖通"),
    LOCAL(7, "LOCAL","独立站"),
    UNKUOWN(-1, "unknown","未知"),
    ;


    public static final Integer[] ARRAYS = (Integer[])Arrays.stream(values()).map(CrossPlatformEnum::getValue).toArray(Integer[]::new);
    private Integer value;
    private String code;
    private String name;


    public static CrossPlatformEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(platform -> platform.getValue().equals(value), CrossPlatformEnum.values());
    }

    public static CrossPlatformEnum of(String code) {
        return Arrays.stream(CrossPlatformEnum.values()).filter(p -> p.code.equalsIgnoreCase(code)).findFirst().orElse(CrossPlatformEnum.UNKUOWN);
    }

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
