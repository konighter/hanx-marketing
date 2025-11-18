package com.hzltd.module.erplus.service.authorization;

import com.hzltd.module.erplus.model.authorization.AuthorizationModel;

public interface AuthorizationApi {

    /**
     * 获取认证地址, 一般grantType=code使用
     * @param authorizationModel
     * @return
     */
    public String grantAuthInfo(AuthorizationModel authorizationModel);

    /**
     * code 换取 AccessToken
     * @param authorizationModel
     * @return
     */
    public AuthorizationModel grantAccessToken(AuthorizationModel authorizationModel);

    /**
     * refreshToken 换取AccessToken
     * @param authorizationModel
     * @return
     */
    public AuthorizationModel refreshAccessToken(AuthorizationModel authorizationModel);

    /**
     * 更新refreshToken
     * @param authorizationModel
     * @return
     */
    public AuthorizationModel renewRefreshToken(AuthorizationModel authorizationModel);
}
