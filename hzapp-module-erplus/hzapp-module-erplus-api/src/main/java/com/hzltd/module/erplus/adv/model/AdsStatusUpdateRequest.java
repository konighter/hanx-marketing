package com.hzltd.module.erplus.adv.model;

import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;
import lombok.Data;

@Data
public class AdsStatusUpdateRequest {

    private AdsEntityTypeEnum entityType; // CAMPAIGN, ADGROUP, AD, KEYWORD, TARGET

    private String entityId;

    private Long localId;

    private String profileId;

    private String status;

}
