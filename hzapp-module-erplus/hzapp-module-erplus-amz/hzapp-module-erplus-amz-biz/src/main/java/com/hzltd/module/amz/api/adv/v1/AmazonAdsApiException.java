package com.hzltd.module.amz.api.adv.v1;

import lombok.Getter;

/**
 * Amazon Ads API 异常
 */
@Getter
public class AmazonAdsApiException extends RuntimeException {

    private final int statusCode;
    private final String responseBody;
    private final String url;

    public AmazonAdsApiException(int statusCode, String responseBody, String url) {
        super(String.format("Amazon Ads API error: %s [%d] %s", url, statusCode, responseBody));
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.url = url;
    }
}
