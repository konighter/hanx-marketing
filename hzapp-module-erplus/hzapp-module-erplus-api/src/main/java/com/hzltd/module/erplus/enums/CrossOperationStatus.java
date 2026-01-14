package com.hzltd.module.erplus.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * 操作状态
 */
@Getter
@RequiredArgsConstructor
public enum CrossOperationStatus implements IntArrayValuable {
    /**
     * 已操作
     */
    ACCEPTED(1, "成功", "ACCEPTED"),
    /**
     * 未操作
     */
    PENDING(0, "未操作", "PENDING"),

    /**
     * 无效
     */
    INVALID(-1, "无效", "INVALID"),

    /**
     * 有效
     */
    VALID(2, "有效", "VALID");



;
    private final Integer status;
    private final String name;
    private final String code;

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CrossOperationStatus::getStatus).toArray();
    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static CrossOperationStatus of(String code) {
        return Arrays.stream(values()).filter(item -> item.getCode().equals(code)).findFirst().orElse(null);
    }

    public static CrossOperationStatus of(Integer status) {
        return Arrays.stream(values()).filter(item -> item.getStatus().equals(status)).findFirst().orElse(null);
    }
}
