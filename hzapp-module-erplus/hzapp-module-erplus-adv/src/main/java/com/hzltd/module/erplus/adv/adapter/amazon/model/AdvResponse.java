package com.hzltd.module.erplus.adv.adapter.amazon.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdvResponse<T> {

    private Integer status;

    private String message;

    private T data;


    public static <T> AdvResponse<T> success(T data) {
        return new AdvResponse<T>(200, "success", data);
    }


}
