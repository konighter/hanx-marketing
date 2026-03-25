package com.hzltd.module.spapi.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum CrossProductStatus implements IntArrayValuable {

    INIT(0, "创建"),

    AUDITING(10, "审核中"),
    AUDIT_FAIL(11, "审核失败"),
    AUDIT_SUCCESS(19, "审核成功"),



    OFFSITE(91, "下架"),
    ONSITE(99,"在线");

    private final Integer status;
    private final String name;


    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(CrossProductStatus::getStatus).toArray();
    @Override
    public int[] array() {
        return ARRAYS;
    }
}
