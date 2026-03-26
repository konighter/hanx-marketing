package com.hzltd.module.erplus.adv.mas.controller.admin.vo;

import lombok.Data;

@Data
public class ActivateReqVO {
    private String skillCode;
    private String targetBizId;
    private String configParams; // JSON String
}
