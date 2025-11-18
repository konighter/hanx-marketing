package com.hzltd.module.erplus.api.adptor.amz;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.api.annotations.ServiceRegister;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
import com.hzltd.module.erplus.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.service.authorization.AuthorizationApi;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.CROSS_API_ERROR;

@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = AuthorizationApi.class)
public class AmazonAuthService implements AuthorizationApi {

    private String host = "https://api.amazon.com";

    private OkHttpClient client = new OkHttpClient();

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
        Request request = new Request.Builder()
                .url(host + "/auth/o2/token")
                .post(new FormBody.Builder()
                        .add("grant_type", "refresh_token")
                        .add("client_id", authorizationModel.getAppKey())
                        .add("client_secret", authorizationModel.getAppSecret())
                        .add("refresh_token", authorizationModel.getRefreshToken())
                        .build())
                .build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw exception(CROSS_API_ERROR, "refresh access token failed, code: " + response.code() + ", message: " + response.message());
            }
            String respBody = response.body().string();
            return  JsonUtils.parseObject(respBody, AuthorizationModel.class);

        } catch (IOException e) {
            throw exception(CROSS_API_ERROR, "refresh access token failed");
        }
    }

    @Override
    public AuthorizationModel renewRefreshToken(AuthorizationModel authorizationModel) {
        return null;
    }
}
