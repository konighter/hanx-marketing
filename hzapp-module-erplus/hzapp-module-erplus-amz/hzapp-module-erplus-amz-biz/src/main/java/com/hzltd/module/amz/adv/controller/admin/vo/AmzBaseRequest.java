package com.hzltd.module.amz.adv.controller.admin.vo;

import lombok.Data;

@Data
public class AmzBaseRequest {
    private Integer shopId;
    private String planId;
    private String cursor;
    private Integer pageSize;
}
