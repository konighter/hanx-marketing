package com.hzltd.module.erplus.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 商品 SPU 状态
 *
 * @author 翰展科技
 */
@Getter
@AllArgsConstructor
public enum ProductSpuStatusEnum implements IntArrayValuable {

    RECYCLE(-1, "回收站"),
    COLLECTED(0, "已采集"),
    CLAIMED(1, "已认领");

    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(ProductSpuStatusEnum::getStatus).toArray();

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

    /**
     * 判断是否处于【上架】状态
     *
     * @param status 状态
     * @return 是否处于【上架】状态
     */
    public static boolean isEnable(Integer status) {
        return CLAIMED.getStatus().equals(status);
    }

}
