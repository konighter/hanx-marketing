package com.hzltd.module.erplus.controller.admin.cross.vo;

import cn.hutool.core.date.DateUtil;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.hzltd.framework.common.pojo.PageParam;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CrossOrderPageRequest extends PageParam {

    private Integer platformId;

    private Integer shopId;

    private String marketId;

    private Integer fulfillType;

    private List<Long> orderIds;

    private String platformOrderId;

    private String sellerSkuCode;

    private String platformProductCode;

    private String title;

    private String keyword;

    private Integer status;

    private LocalDateTime createTimeStart;

    private LocalDateTime createTimeEnd;

    private LocalDateTime updateTimeStart;

    private LocalDateTime updateTimeEnd;

    private Long orderTimeStart;

    private Long orderTimeEnd;

    private String[] createTimeRange;

    private String[] updateTimeRange;

    @JsonSetter("createTimeStart")
    public void setCreateTimeStartFromJson(String value) {
        if (value != null && !value.isEmpty()) {
            this.createTimeStart = DateUtil.parseLocalDateTime(value);
        }
    }

    @JsonSetter("createTimeEnd")
    public void setCreateTimeEndFromJson(String value) {
        if (value != null && !value.isEmpty()) {
            this.createTimeEnd = DateUtil.parseLocalDateTime(value);
        }
    }

    public void setCreateTimeRange(String[] createTimeRange) {
        if (createTimeRange == null || createTimeRange.length == 0) {
            return;
        }
        if (createTimeRange.length >= 1) {
            this.createTimeStart = DateUtil.parseLocalDateTime(createTimeRange[0]);
            this.orderTimeStart = DateUtil.parse(createTimeRange[0]).getTime();
        }
        if (createTimeRange.length >= 2) {
            this.createTimeEnd = DateUtil.parseLocalDateTime(createTimeRange[1]);
            this.orderTimeEnd = DateUtil.parse(createTimeRange[1]).getTime();
        }
    }

    public void setUpdateTimeRange(String[] updateTimeRange) {
        if (updateTimeRange == null) {
            return;
        }
        if (updateTimeRange.length == 2) {
            this.updateTimeStart = DateUtil.parseLocalDateTime(updateTimeRange[0]);
            this.updateTimeEnd = DateUtil.parseLocalDateTime(updateTimeRange[1]);
        } else if (updateTimeRange.length == 1) {
            this.updateTimeStart = DateUtil.parseLocalDateTime(updateTimeRange[0]);
        }
    }


}
