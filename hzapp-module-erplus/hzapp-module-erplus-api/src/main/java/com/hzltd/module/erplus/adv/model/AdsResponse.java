package com.hzltd.module.erplus.adv.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 广告平台 通用响应 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsResponse<T> implements Serializable {

    private boolean success;

    private String message;

    private T data;

    public static <T> AdsResponse<T> success(T data) {
        return AdsResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> AdsResponse<T> error(String message) {
        return AdsResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }
}
