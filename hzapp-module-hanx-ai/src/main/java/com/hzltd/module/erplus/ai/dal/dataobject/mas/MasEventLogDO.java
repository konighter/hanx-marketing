package com.hzltd.module.erplus.ai.dal.dataobject.mas;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * MAS Execution Event Log DO.
 * Captures real-time transitions and actions for visual debugging.
 */
@TableName("ai_mas_event_log")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasEventLogDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 会话 ID
     */
    private String sessionId;
    /**
     * 循环 ID (Graph Node)
     */
    private String loopId;
    /**
     * 事件类型 (e.g. START, PHASE_CHANGE, TOOL_CALL, ERROR, DONE)
     */
    private String eventType;
    /**
     * 事件负载 (JSON or Text)
     */
    private String payload;
    /**
     * 事件描述
     */
    private String description;

}
