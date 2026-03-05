package com.hzltd.module.erplus.adv.adapter.model;

import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;
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
