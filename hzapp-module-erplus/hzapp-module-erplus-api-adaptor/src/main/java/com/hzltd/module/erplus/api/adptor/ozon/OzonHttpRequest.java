package com.hzltd.module.erplus.api.adptor.ozon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.model.authorization.AuthorizationModel;
import okhttp3.*;

import java.io.IOException;
import java.util.Map;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.CROSS_API_ERROR;

class OzonHttpRequest {

    private static OkHttpClient client = new OkHttpClient();

    private static final String HOST = "https://api-seller.ozon.ru";

    public static <R> String request(String url, R req, Map<String, String> headers) {
        Request request = new Request.Builder()
                .url(HOST + url)
                .post(RequestBody.create(JsonUtils.toJsonString(req), MediaType.parse("application/json")))
                .headers(Headers.of(headers))
                .build();
        try (Response response = client.newCall(request).execute()) {
//            if (!response.isSuccessful()) {
//                throw exception(CROSS_API_ERROR, "refresh access token failed, code: " + response.code() + ", message: " + response.message());
//            }
            return  response.body().string();
        } catch (IOException e) {
            throw exception(CROSS_API_ERROR, e.getMessage());
        }
    }




}
