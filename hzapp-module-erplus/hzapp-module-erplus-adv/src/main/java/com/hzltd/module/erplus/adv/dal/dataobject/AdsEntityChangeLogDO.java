package com.hzltd.module.erplus.adv.dal.dataobject;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 广告实体变更日志 DO (审计表)
 *
 * @author hzadd
 */
@TableName(value = "ads_entity_change_log", autoResultMap = true)
@KeySequence("ads_entity_change_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsEntityChangeLogDO extends BaseDO {

    @TableId
    private Long id;
    /** 店铺ID */
    private Long shopId;
    /** 关联广告账户ID */
    private Long accountId;
    /** 实体类型: CAMPAIGN / ADGROUP / AD / KEYWORD / CREATIVE */
    private String entityType;
    /** 内部实体ID */
    private Long entityId;
    /** 操作类型: STATUS_CHANGE / BUDGET_CHANGE / BID_CHANGE / CREATIVE_REPLACE / TARGETING_CHANGE / METADATA_SYNC / ENTITY_CREATE / ENTITY_DELETE */
    private String action;
    /** 变更字段名 */
    private String fieldName;
    /** 变更前的值 */
    private String oldValue;
    /** 变更后的值 */
    private String newValue;
    /** 变更来源: PLATFORM_SYNC / USER / AI_AGENT */
    private String source;
    /** 操作人 */
    private String operator;
    /** 关联的平台API请求ID */
    private String requestId;
    /** 备注 */
    private String remark;
}
