package com.hzltd.module.erplus.system.enums;

import cn.hutool.core.util.ArrayUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthorizationType  {
    SELF_AUTH(1, "平台自授权"),
    USER_AUTH(0, "用户授权"),
    ;

    public static AuthorizationType valueOf(Integer type) {
        return ArrayUtil.firstMatch(platform -> platform.getType().equals(type), AuthorizationType.values());
    }

    /**
     * 类型
     */
    private final Integer type;
    /**
     * 名称
     */
    private final String name;



}
