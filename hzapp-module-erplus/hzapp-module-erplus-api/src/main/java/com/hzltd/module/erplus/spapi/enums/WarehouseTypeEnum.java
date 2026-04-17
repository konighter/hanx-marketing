package com.hzltd.module.erplus.spapi.enums;

import com.hzltd.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仓库类型
 *
 *  {value: 0, lable: '平台仓', exclusive : true},
 * {value: 1, lable: '海外仓', exclusive : false},
 * {value: 2, lable: '家庭仓', exclusive : false},
 * {value: 3, lable: '活动仓', exclusive : true},
 */
@Getter
@AllArgsConstructor
public enum WarehouseTypeEnum implements IntArrayValuable {

    /**
     * 平台仓
     */
    PLATFORM(0, "平台仓"),
    /**
     * 海外仓
     */
    OVERSEAS(1, "海外仓"),
    /**
     * 家庭仓
     */
    FAMILY(2, "家庭仓"),
    /**
     * 活动仓
     */
    ACTIVITY(3, "活动仓"),

    /**
     * 本地仓
     */
    LOCAL(4, "本地仓"),
    /**
     * 其他
     */
    OTHER(99, "其他");
    /**
     * 类型值
     */
    private final Integer value;
    private final String name;


    @Override
    public int[] array() {
        return new int[0];
    }

    public static WarehouseTypeEnum of(Integer code) {
        for (WarehouseTypeEnum value : values()) {
            if (value.getValue().equals(code)) {
                return value;
            }
        }
        return OTHER;
    }

}
