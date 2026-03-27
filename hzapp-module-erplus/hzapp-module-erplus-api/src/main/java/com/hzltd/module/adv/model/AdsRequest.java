package com.hzltd.module.adv.model;

import com.hzltd.module.system.enums.AdsPlatformEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdsRequest <T> {

    private AdsPlatformEnum platform;


    /**
     * Unix timestamp
     */
    private Long timestamp;

    private T request;


}
