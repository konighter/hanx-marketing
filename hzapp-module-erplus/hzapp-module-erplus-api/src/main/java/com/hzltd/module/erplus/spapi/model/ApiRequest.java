package com.hzltd.module.erplus.spapi.model;

import com.hzltd.module.erplus.spapi.enums.LanguageEnum;
import com.hzltd.module.erplus.spapi.model.authorization.AuthorizationModel;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.BaseCrossRequest;
import lombok.Data;

import java.util.Locale;

@Data
public class ApiRequest<T> {

    /**
     * 跨境平台类型
     */
    private CrossPlatformEnum crossPlatform;

    /**
     * 店铺ID
     */
    private String shopId;

    /**
     * Auth 可以从shopId取，也可以自己带
     */
    private AuthorizationModel authorizationModel;

    public ApiRequest<T> setShopIdInt(Integer shopId) {
        this.shopId = String.valueOf(shopId);
        return this;
    }

    /**
     * locale属性, 每个市场可能对应不同的Locale
     * 1、涉及接口当地时间等的转换：比如 AMZ US 请求时间为当地8:00～12:00 UK 为 9:00～12:00等
     */
    private Locale locale = Locale.SIMPLIFIED_CHINESE;

    /**
     * 文本翻译目标语言
     */
    private LanguageEnum language = LanguageEnum.ZH_CN;

    /**
     * 市场ID
     */
    private String marketId;

    /**
     * 区域
     */
    private String regen;

    /**
     * Unix timestamp
     */
    private Long timestamp;

    /**
     * 时区
     */
    private String timeZone;

    /**
     * 请求体
     */
    private T request;

    public static <T extends BaseCrossRequest, R> ApiRequest<R> of(T request, R req) {
        ApiRequest<R> apiRequest = new ApiRequest<>();
        apiRequest.setLanguage(request.getLanguage());
        apiRequest.setCrossPlatform(CrossPlatformEnum.valueOf(request.getPlatformId()));
        apiRequest.setShopId(String.valueOf(request.getShopId()));
        apiRequest.setTimestamp(System.currentTimeMillis() / 1000);
        apiRequest.setRequest(req);
        return apiRequest;
    }

}
