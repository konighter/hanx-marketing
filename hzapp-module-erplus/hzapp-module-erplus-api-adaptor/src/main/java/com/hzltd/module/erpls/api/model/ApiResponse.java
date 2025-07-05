package com.hzltd.module.erpls.api.model;

import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;

@Data
public class ApiResponse<T> {

    private String requestId;
    private Integer code;
    private String msg;
    private PageParam page;

    private T data;

}
