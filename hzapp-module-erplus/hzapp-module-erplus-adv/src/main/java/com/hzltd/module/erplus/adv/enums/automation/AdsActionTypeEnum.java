package com.hzltd.module.erplus.adv.enums.automation;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 广告自动化动作枚举 (平台无关)
 */
@Getter
@AllArgsConstructor
public enum AdsActionTypeEnum {

    CREATE_KEYWORD("CREATE_KEYWORD", "创建关键词"),
    NEGATE_TERM("NEGATE_TERM", "否定搜索词"),
    ADJUST_BID("ADJUST_BID", "调整竞价"),
    UPDATE_STATUS("UPDATE_STATUS", "更新状态");

    private final String type;
    private final String name;

}
