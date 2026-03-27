package com.hzltd.module.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OAuth 授权类型枚举
 */
@Getter
@AllArgsConstructor
public enum OAuthGrantTypeEnum {

    AUTHORIZATION_CODE("authorization_code", "授权码模式"),
    REFRESH_TOKEN("refresh_token", "刷新令牌模式");

    /**
     * 类型
     */
    private final String grantType;
    /**
     * 名称
     */
    private final String name;

}
