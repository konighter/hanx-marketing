package com.hzltd.module.spapi.model.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hzltd.module.system.model.SellPlatformModel;
import com.hzltd.module.system.model.ShopModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorizationModelV0 {
    private ShopModel shopModel;
    private SellPlatformModel platformModel;

    @JsonProperty("auth_scope")
    private String authScope;

    @JsonProperty("region")
    private String region;

    @JsonProperty("app_key")
    private String appKey;

    @JsonProperty("app_secret")
    private String appSecret;

    @JsonProperty("grant_code")
    private String grantCode;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("token_type")
    private String tokenType;

    @JsonProperty("expires_in")
    private Integer expireIn;

    private String state;

    @JsonProperty("grant_type")
    private String grantType;



    /**
     * 卖家标记, 自授权输入, OAuth授权Api获取
     */
    @JsonProperty("seller_id")
    private String sellerId;

    /**
     * 是否自授权
     */
    @JsonProperty("self_auth")
    private Boolean selfAuth;
}
