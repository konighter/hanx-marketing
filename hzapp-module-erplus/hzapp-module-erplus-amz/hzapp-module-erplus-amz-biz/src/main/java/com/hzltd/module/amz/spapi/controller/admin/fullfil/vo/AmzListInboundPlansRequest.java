package com.hzltd.module.amz.spapi.controller.admin.fullfil.vo;

import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;

@Data
public class AmzListInboundPlansRequest extends PageParam {

    private String planId;

     /**
     * 店铺ID
     */
    private Integer shopId;

     /**
     * 市场ID
     */
    private String marketId;
     /**
     * 分页游标
     */
    private String cursor;

     /**
     * 分页大小
     */
    private Integer pageSize;

}
