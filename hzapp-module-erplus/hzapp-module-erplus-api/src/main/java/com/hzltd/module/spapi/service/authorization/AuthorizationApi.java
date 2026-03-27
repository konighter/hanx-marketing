package com.hzltd.module.spapi.service.authorization;

import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.spapi.model.authorization.AuthorizationModelV0;

public interface AuthorizationApi {

    /**
     * 获取认证地址, 一般grantType=code使用
     * @param authorizationModel
     * @return
     */
    public String grantAuthInfo(AuthorizationModelV0 authorizationModel);

    /**
     * code 换取 AccessToken
     * @param authorizationModel
     * @return
     */
    public AuthorizationModelV0 grantAccessToken(AuthorizationModelV0 authorizationModel);

    /**
     * refreshToken 换取AccessToken
     * @param authorizationModel
     * @return
     */
    public AuthorizationModelV0 refreshAccessToken(AuthorizationModelV0 authorizationModel);

    /**
     * 更新refreshToken
     * @param authorizationModel
     * @return
     */
    public AuthorizationModelV0 renewRefreshToken(AuthorizationModelV0 authorizationModel);


    default void postAuthorization(AuthorizationModel authorizationModel){}


}
