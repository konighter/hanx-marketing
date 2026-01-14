package com.hzltd.module.erplus.model.inventory;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 预占库存所有者
 */
@Getter
@AllArgsConstructor
public enum PreOwner implements IntArrayValuable {
    PLATFORM(0, "平台"),
    SELLER(1, "卖家"),
    OTHER(9, "其他");


    private final Integer status;
    private final String name;


    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(PreOwner::getStatus).toArray();

    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static PreOwner of(Integer status) {
        return Arrays.stream(values()).filter(item -> item.getStatus().equals(status)).findFirst().orElse(OTHER);
    }
}
