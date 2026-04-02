package com.hzltd.module.erplus.adv.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 广告平台 Token 交换/刷新结果
 */
@Data
@Builder
public class AdsTokenResult {
    private String accessToken;
    private String refreshToken;
    private LocalDateTime expiresAt;
    /**
     * 外部平台账号 ID
     */
    private String externalAccountId;
    private String scope;
    private String tokenType;
}
