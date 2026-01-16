package com.hzltd.module.erplus.controller.admin.amz.vo;

import lombok.Data;

@Data
public class AmzBaseRequest {
    private Integer shopId;
    private String planId;
    private String cursor;
    private Integer pageSize;
}
