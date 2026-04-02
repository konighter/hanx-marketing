package com.hzltd.module.erplus.service.authorization;

import com.hzltd.module.erplus.dal.dataobject.authorization.PlatformAuthDO;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModelV0;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.enums.OAuthGrantTypeEnum;

/**
 * 内部授权服务接口，抽象OAuth逻辑
 * 用于处理各平台（Amazon, TikTok等）的授权链接获取、Token换取及刷新
 */
public interface AuthInternalService {

    /**
     * 获取授权链接
     *
     * @param platform 平台类型
     * @param region   区域
     * @param authType 授权范围 (AMAZON_SP, AMAZON_ADV等)
     * @param state    状态标识
     * @return 授权跳转地址
     */
    String getAuthLink(CrossPlatformEnum platform, Long appId,  String region, String authType, String state);

    /**
     * 换取AccessToken (适用于 code 换取 或 refreshToken 换取)
     *
     * @param platform           平台类型
     * @param region             区域
     * @param codeOrRefreshToken 授权码或刷新令牌
     * @param grantType          授权类型 {@link OAuthGrantTypeEnum}
     * @param appKey             应用 Key (可选)
     * @param appSecret          应用 Secret (可选)
     * @param sellerId           卖家 ID (可选)
     * @return 授权信息模型
     */
    AuthorizationModelV0 getAccessToken(CrossPlatformEnum platform, String region, String authType, String codeOrRefreshToken, OAuthGrantTypeEnum grantType,
                                        String appKey, String appSecret, String sellerId);

    /**
     * 刷新AccessToken
     *
     * @param platform     平台类型
     * @param region       区域
     * @param refreshToken 刷新令牌
     * @return 授权信息模型
     */
    AuthorizationModelV0 refreshAccessToken(CrossPlatformEnum platform, String region, String refreshToken);

    /**
     * 初始化账号
     */
    void initAccount(PlatformAuthDO authDO);
}
