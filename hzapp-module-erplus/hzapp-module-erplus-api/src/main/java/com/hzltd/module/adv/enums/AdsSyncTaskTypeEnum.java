package com.hzltd.module.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 同步任务类型枚举
 *
 * @author hzadd
 */
@Getter
@AllArgsConstructor
public enum AdsSyncTaskTypeEnum {

    METADATA_FULL("METADATA_FULL", "元数据全量同步"),
    METADATA_INCR("METADATA_INCR", "元数据增量同步"),
    REPORT_DAILY("REPORT_DAILY", "每日报表同步"),
    REPORT_DIMENSION("REPORT_DIMENSION", "维度报表子任务"),
    TOKEN_REFRESH("TOKEN_REFRESH", "Token刷新");

    private final String code;
    private final String name;
}
