package com.hzltd.module.erplus.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RelationLevel implements IntArrayValuable {
    CHILD(1, "子项"),
    PARENT(2, "父项"),
    ;
    private final Integer status;
    private final String name;




    public static final int[] ARRAYS = Arrays.stream(values()).mapToInt(RelationLevel::getStatus).toArray();

    @Override
    public int[] array() {
        return ARRAYS;
    }

    public static RelationLevel of(Integer status) {
        return Arrays.stream(values()).filter(item -> item.getStatus().equals(status)).findFirst().orElse(null);
    }
}
