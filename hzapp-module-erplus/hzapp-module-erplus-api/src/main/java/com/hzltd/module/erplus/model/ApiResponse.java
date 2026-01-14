package com.hzltd.module.erplus.model;

import com.hzltd.framework.common.enums.CommonStatusEnum;
import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class ApiResponse<T> {

    private String requestId;
    private Integer code;
    private String msg;
    private PageParam page;
    private String cursor;// 分页游标
    private Integer total;// 总条数

    private T data;

    public boolean hasNext() {
        return StringUtils.hasText(this.cursor) || page != null && page.getPageSize() * page.getPageNo() < total;
    }

    public boolean success() {
        return CommonStatusEnum.isEnable(this.code);
    }

    public static <R> ApiResponse<R> success(R data) {
        ApiResponse<R> resp = new ApiResponse<>();
        resp.setData(data);
        resp.setCode(CommonStatusEnum.ENABLE.getStatus());
        return resp;
    }

    public static <R> ApiResponse<R> error(String message) {
        ApiResponse<R> resp = new ApiResponse<>();
        resp.setCode(CommonStatusEnum.DISABLE.getStatus());
        resp.setMsg(message);
        return resp;
    }
}
