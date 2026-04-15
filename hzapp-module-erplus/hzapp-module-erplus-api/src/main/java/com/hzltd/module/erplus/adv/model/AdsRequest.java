package com.hzltd.module.erplus.adv.model;

import com.hzltd.module.erplus.system.enums.AdsPlatformEnum;
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
