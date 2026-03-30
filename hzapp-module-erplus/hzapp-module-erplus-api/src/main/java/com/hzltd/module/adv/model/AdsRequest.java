package com.hzltd.module.adv.model;

import com.hzltd.module.system.enums.AdsPlatformEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsRequest <T> {

    private AdsPlatformEnum platform;

    /**
     * Shop ID
     */
    private Long shopId;

    /**
     * Unix timestamp
     */
    private Long timestamp;

    private T request;


}
