package com.hzltd.module.amz.api.spapi;

import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.api.annotations.ServiceRegister;
import com.hzltd.module.system.enums.CrossPlatformEnum;
import com.hzltd.module.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.spapi.service.authorization.AuthorizationApi;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.system.enums.ErplusErrorCodeConstants.CROSS_API_ERROR;

@Service
@ServiceRegister(platform = CrossPlatformEnum.AMAZON, serviceClass = AuthorizationApi.class)
public class AmazonAuthService implements AuthorizationApi {

    private String host = "https://api.amazon.com";

    private String grantAuthHost = "https://www.amazon.com/ap/oa";

    private OkHttpClient client = new OkHttpClient();

    @Override
    public String grantAuthInfo(AuthorizationModel authorizationModel) {
        return new StringBuilder(grantAuthHost)
                .append("?response_type=code")
                .append("&scope=advertising::campaign_management")
                .append("&client_id=").append(authorizationModel.getAppKey())
                .append("&redirect_uri=").append("http://localhost")
                .append("&state=").append(authorizationModel.getState())
                .toString();
    }

    @Override
    public AuthorizationModel grantAccessToken(AuthorizationModel authorizationModel) {
        Request request = new Request.Builder()
                .url(host + "/auth/o2/token")
                .post(new FormBody.Builder()
                        .add("grant_type", "authorization_code")
                        .add("client_id", authorizationModel.getAppKey())
                        .add("client_secret", authorizationModel.getAppSecret())
                        .add("code", authorizationModel.getGrantCode())
                        .add("redirect_uri", "http://localhost")
                        .build())
                .build();



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
