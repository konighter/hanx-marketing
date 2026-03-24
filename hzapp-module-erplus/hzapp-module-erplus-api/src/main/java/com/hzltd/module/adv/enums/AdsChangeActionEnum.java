package com.hzltd.module.adv.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 变更操作类型枚举
 *
 * @author hzadd
 */
@Getter
@AllArgsConstructor
public enum AdsChangeActionEnum {

    STATUS_CHANGE("STATUS_CHANGE", "状态变更"),
    BUDGET_CHANGE("BUDGET_CHANGE", "预算变更"),
    BID_CHANGE("BID_CHANGE", "出价变更"),
    CREATIVE_REPLACE("CREATIVE_REPLACE", "创意替换"),
    TARGETING_CHANGE("TARGETING_CHANGE", "定向变更"),
    METADATA_SYNC("METADATA_SYNC", "元数据同步"),
    ENTITY_CREATE("ENTITY_CREATE", "实体创建"),
    ENTITY_DELETE("ENTITY_DELETE", "实体删除");

    private final String code;
    private final String name;
}
