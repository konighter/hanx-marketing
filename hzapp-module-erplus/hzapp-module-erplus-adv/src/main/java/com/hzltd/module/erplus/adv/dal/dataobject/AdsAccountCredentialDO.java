package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 广告账户凭证 DO
 *
 * @author hzadd
 */
@TableName(value = "ads_account_credential", autoResultMap = true)
@KeySequence("ads_account_credential_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAccountCredentialDO extends BaseDO {

    @TableId
    private Long id;

    /**
     * 凭证类型: OAUTH2 / API_KEY / LWA
     */
    private String credentialType;

    /**
     * 广告平台: AMAZON / META / GOOGLE / TIKTOK
     */
    private String platform;

    /**
     * 加密后的 Access Token
     */
    private String accessToken;

    /**
     * 加密后的 Refresh Token
     */
    private String refreshToken;

    /**
     * Token 过期时间
     */
    private LocalDateTime tokenExpiresAt;

    /**
     * OAuth Client ID
     */
    private String clientId;

    /**
     * 加密后的 Client Secret
     */
    private String clientSecret;

    /**
     * 其他凭据 (JSON)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Object extCredential;
}
