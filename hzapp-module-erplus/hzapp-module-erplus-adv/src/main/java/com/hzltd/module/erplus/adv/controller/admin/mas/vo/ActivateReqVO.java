package com.hzltd.module.erplus.adv.controller.admin.mas.vo;

import lombok.Data;

@Data
public class ActivateReqVO {
    private String skillCode;
    private String targetBizId;
    private String configParams; // JSON String
}
