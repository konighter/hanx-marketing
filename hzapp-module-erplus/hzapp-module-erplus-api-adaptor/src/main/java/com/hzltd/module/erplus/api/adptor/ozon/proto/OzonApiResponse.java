package com.hzltd.module.erplus.api.adptor.ozon.proto;

import lombok.Data;

import java.util.List;

@Data
public class OzonApiResponse<T> {
    private T result;

    private Integer code;
    private String message;
    private List<String> details;

}
