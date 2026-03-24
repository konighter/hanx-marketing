package com.hzltd.module.adv.model;

import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AdsBudgetUpdateRequest {
    private AdsEntityTypeEnum entityType; // Always CAMPAIGN for now
    private String entityId;
    private Long localId;
    private String profileId;
    private BigDecimal budget;
}
