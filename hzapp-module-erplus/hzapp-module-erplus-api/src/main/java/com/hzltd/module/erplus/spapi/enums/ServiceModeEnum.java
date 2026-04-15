package com.hzltd.module.erplus.spapi.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ServiceModeEnum {
    FBA("FBA", "FBA"),
    FBM("FBM", "FBM"),
    SO("SO", "半托管"),
    FSO("FSO", "全托管"),
    ;


    /**
     * code
     */
    private final String code;
    /**
     * 名称
     */
    private final String name;


    public static ServiceModeEnum getByCode(String code) {
        for (ServiceModeEnum serviceModeEnum : ServiceModeEnum.values()) {
            if (serviceModeEnum.getCode().equals(code)) {
                return serviceModeEnum;
            }
        }
        return null;
    }
}
