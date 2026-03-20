package com.hzltd.module.erplus.sys.controller.admin.notify.vo;

import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 通知渠道 分页查询 Request VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NotifyChannelPageReqVO extends PageParam {

    /**
     * 渠道名称 (模糊匹配)
     */
    private String name;

    /**
     * 渠道类型
     */
    private Integer channelType;

    /**
     * 状态
     */
    private Integer status;
}
