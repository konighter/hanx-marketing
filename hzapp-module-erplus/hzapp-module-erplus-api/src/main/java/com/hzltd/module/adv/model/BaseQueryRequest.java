package com.hzltd.module.adv.model;

import lombok.Data;

@Data
public class BaseQueryRequest {

    private Integer limit;

    /**
     * 查询索引
     */
    private String nextToken;

    /**
     * 开始时间, 毫秒
     */
    private Long startAt;

    /**
     * 结束时间, 毫秒
     */
    private Long endAt;

    /**
     * Page or All
     */
    private String queryType;


}
