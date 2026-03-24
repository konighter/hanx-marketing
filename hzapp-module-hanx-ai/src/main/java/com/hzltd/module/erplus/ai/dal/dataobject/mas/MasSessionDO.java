package com.hzltd.module.erplus.ai.dal.dataobject.mas;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * MAS 会话主表 DO
 */
@TableName("ai_mas_session")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasSessionDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 会话唯一标识 (UUID)
     */
    private String sessionId;
    /**
     * 顶层任务目标
     */
    private String goal;
    /**
     * 当前状态
     */
    private String status;
    /**
     * 发起用户 ID
     */
    private Long userId;

}
