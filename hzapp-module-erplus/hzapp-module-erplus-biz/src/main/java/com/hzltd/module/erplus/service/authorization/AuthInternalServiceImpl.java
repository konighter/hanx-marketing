package com.hzltd.module.erplus.service.authorization;

import com.hzltd.framework.common.util.object.BeanUtils;
import com.hzltd.module.erplus.api.service.AuthorizationApiFactory;
import com.hzltd.module.erplus.dal.dataobject.authorization.PlatformAuthDO;
import com.hzltd.module.erplus.dal.dataobject.shop.PlatformAppDO;
import com.hzltd.module.erplus.service.shop.PlatformAppService;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.spapi.model.authorization.AuthorizationModelV0;
import com.hzltd.module.spapi.service.authorization.AuthorizationApi;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.system.enums.OAuthGrantTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 内部授权服务实现类
 */
@Slf4j
@Service
public class AuthInternalServiceImpl implements AuthInternalService {

    @Resource
    private AuthorizationApiFactory authorizationApiFactory;
    @Resource
    private PlatformAppService appService;

    @Override
    public String getAuthLink(CrossPlatformEnum platform, String region, String authScope, String state) {
        AuthorizationApi api = authorizationApiFactory.getCrossApiService(platform);
        if (api == null) {
            throw new RuntimeException("Platform not supported for authorization: " + platform);
        }
        
        AuthorizationModelV0 model = new AuthorizationModelV0();
        model.setState(state);
        // Note: For Amazon, authScope might differentiate between SP-API and Advertising
        // This will be fully implemented when AmazonAuthService is updated.
        return api.grantAuthInfo(model);
    }

    @Override
    public AuthorizationModelV0 getAccessToken(CrossPlatformEnum platform, String region, String authScope, String codeOrRefreshToken, OAuthGrantTypeEnum grantType,
                                               String appKey, String appSecret, String sellerId) {
        AuthorizationApi api = authorizationApiFactory.getCrossApiService(platform);
        if (api == null) {
            throw new RuntimeException("Platform not supported for token exchange: " + platform);
        }

        AuthorizationModelV0 model = new AuthorizationModelV0();
        model.setRegion(region);
        model.setAuthScope(authScope);
        model.setGrantType(grantType.getGrantType());
        model.setAppKey(appKey);
        model.setAppSecret(appSecret);
        model.setSellerId(sellerId);
        
        if (OAuthGrantTypeEnum.AUTHORIZATION_CODE.equals(grantType)) {
            model.setGrantCode(codeOrRefreshToken);
            return api.grantAccessToken(model);
        } else if (OAuthGrantTypeEnum.REFRESH_TOKEN.equals(grantType)) {
            model.setRefreshToken(codeOrRefreshToken);
            return api.refreshAccessToken(model);
        }
        
        return null;
    }

    @Override
    public AuthorizationModelV0 refreshAccessToken(CrossPlatformEnum platform, String region, String refreshToken) {
        AuthorizationApi api = authorizationApiFactory.getCrossApiService(platform);
        if (api == null) {
            throw new RuntimeException("Platform not supported for token refresh: " + platform);
        }

        AuthorizationModelV0 model = new AuthorizationModelV0();
        model.setRefreshToken(refreshToken);
        return api.refreshAccessToken(model);
    }

    @Override
    public void initAccount(PlatformAuthDO authDO) {
        String platform = authDO.getPlatform();
        AuthorizationApi api = authorizationApiFactory.getCrossApiService(CrossPlatformEnum.of(platform));
        PlatformAppDO appDO = appService.getPlatformApp(authDO.getAppId());
        if (appDO == null) {
            log.warn("App is Null, appId={}", authDO.getAppId());
            return;
        }
        AuthorizationModel authorizationModel = BeanUtils.toBean(authDO, AuthorizationModel.class);
        authorizationModel.setAppKey(appDO.getAppKey());
        authorizationModel.setAppSecret(appDO.getAppSecret());
        api.postAuthorization(authorizationModel);
    }
}
