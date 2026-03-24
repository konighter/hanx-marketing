package com.hzltd.module.adv.model;

import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdsBidUpdateRequest {
    private AdsEntityTypeEnum entityType; // KEYWORD, TARGET
    private String entityId;
    private Long localId;
    private String profileId;
    private BigDecimal bid;
}
