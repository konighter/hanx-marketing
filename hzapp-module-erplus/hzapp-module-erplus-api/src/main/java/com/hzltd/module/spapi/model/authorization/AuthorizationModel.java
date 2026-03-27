package com.hzltd.module.spapi.model.authorization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 对应PlatformAuthDO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationModel {
    private Long id;
    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 平台类型: AMAZON, TIKTOK
     */
    private String platform;
    /**
     * 员工ID
     */
    private Long userId;
    /**
     * 授权类型: AMAZON_SP, AMAZON_ADV, TTS_SHOP
     */
    private String authType;
    /**
     * 授权范围
     */
    private String authScope;
    /**
     * 区域代码: NA(北美), EU(欧洲), FE(远东), GLOBAL(TTS全球)
     */
    private String region;
    /**
     * 卖家后台的唯一身份标识
     */
    private String sellerId;
    /**
     * 用于刷新access_token的长效令牌
     */
    private String refreshToken;
    /**
     * 当前有效的访问令牌
     */
    private String accessToken;
    /**
     * access_token的过期时间
     */
    private LocalDateTime expiryTime;

    private String appKey;

    private String appSecret;
    /**
     * 平台应用ID
     */
    private Long appId;
}
