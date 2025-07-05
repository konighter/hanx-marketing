package com.hzltd.module.erplus.enums.common;

import cn.hutool.core.util.ArrayUtil;
import com.hzltd.framework.common.core.IntArrayValuable;
import com.hzltd.framework.common.enums.UserTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;


@Getter
@AllArgsConstructor
public enum CrossPlatformEnum implements IntArrayValuable {
    AMZ(1, "亚马逊"),
    OZON(2, "Ozon"),
    TTS(3, "Tiktok Shop");


    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CrossPlatformEnum::getValue).toArray();
    private Integer value;
    private String name;


    public static CrossPlatformEnum valueOf(Integer value) {
        return ArrayUtil.firstMatch(platform -> platform.getValue().equals(value), CrossPlatformEnum.values());
    }

    @Override
    public int[] array() {
        return ARRAYS;
    }
}
