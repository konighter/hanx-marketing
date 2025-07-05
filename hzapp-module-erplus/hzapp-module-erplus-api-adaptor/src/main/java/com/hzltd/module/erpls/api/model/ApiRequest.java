package com.hzltd.module.erpls.api.model;

import com.hzltd.module.erpls.api.constant.LanguageEnum;
import com.hzltd.module.erplus.enums.common.CrossPlatformEnum;
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
     * locale属性, 每个市场可能对应不同的Locale
     * 1、涉及接口当地时间等的转换：比如 AMZ US 请求时间为当地8:00～12:00 UK 为 9:00～12:00等
     */
    private Locale locale;

    /**
     * 文本翻译目标语言
     */
    private LanguageEnum language;

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
    private Integer timestamp;

    /**
     * 请求体
     */
    private T request;

}
