package com.hzltd.module.spapi.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum ProductClaimStatus implements IntArrayValuable {
    //  0-已认领, 待发布， 1-已发布， 9-发布失败
    CLAIM(0, "待发布"), // 已认领, 待发布
    SYNCED(1, "已发布"), // 已发布
    SYNC_FAIL(9, "发布失败"); // 发布失败

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ProductClaimStatus::getStatus).toArray();


    /**
     * 状态
     */
    private final Integer status;
    /**
     * 状态名
     */
    private final String name;

    @Override
    public int[] array() {
        return ARRAYS;
    }




}
