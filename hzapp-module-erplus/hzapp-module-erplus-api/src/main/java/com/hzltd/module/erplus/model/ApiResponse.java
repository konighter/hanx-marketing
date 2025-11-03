package com.hzltd.module.erplus.model;

import com.hzltd.framework.common.enums.CommonStatusEnum;
import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;

@Data
public class ApiResponse<T> {

    private String requestId;
    private Integer code;
    private String msg;
    private PageParam page;

    private T data;

    public boolean success() {
        return CommonStatusEnum.isEnable(this.code);
    }

    public static <R> ApiResponse<R> success(R data) {
        ApiResponse<R> resp = new ApiResponse<>();
        resp.setData(data);
        resp.setCode(CommonStatusEnum.ENABLE.getStatus());
        return resp;
    }
}
