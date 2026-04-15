package com.hzltd.module.erplus.spapi.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作类型
 */
@Getter
@AllArgsConstructor
public enum OperationModeEnum implements IntArrayValuable {
    CREATE(0, "创建"),
    UPDATE(1, "更新"),
    DELETE(2, "删除"),
    UNKNOWN(-1, "未知");

    private final Integer code;
    private final String name;

    @Override
    public int[] array() {
        return new int[0];
    }

    public static OperationModeEnum of(Integer code) {
        for (OperationModeEnum opt: OperationModeEnum.values()) {
            if (opt.code.equals(code)) return opt;
        }
        return UNKNOWN;
    }

}
