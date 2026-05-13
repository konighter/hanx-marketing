package com.hzltd.module.erplus.controller.admin.cross.vo;

import lombok.Data;

@Data
public class CrossOrderStateStatsRespVO {

    private String stateOrRegion;
    
    private String countryCode;

    private Long count;
}
