package com.hzltd.module.erplus.api.adptor.ozon;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.api.annotations.ServiceRegister;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.service.authorization.AuthorizationApi;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.CROSS_API_ERROR;

@Service
@ServiceRegister(platform = CrossPlatformEnum.OZON, serviceClass = AuthorizationApi.class)
public class OzonAuthService implements AuthorizationApi {
    private static final Integer DEFAULT_EXPIRE_IN = 60 * 60 * 24 * 365 * 10;

    @Override
    public String grantAuthInfo(AuthorizationModel authorizationModel) {
        return "";
    }

    @Override
    public AuthorizationModel grantAccessToken(AuthorizationModel authorizationModel) {

        return null;
    }

    @Override
    public AuthorizationModel refreshAccessToken(AuthorizationModel authorizationModel) {
        authorizationModel.setExpireIn(DEFAULT_EXPIRE_IN);
        return authorizationModel;
    }

    @Override
    public AuthorizationModel renewRefreshToken(AuthorizationModel authorizationModel) {
        return null;
    }
}
