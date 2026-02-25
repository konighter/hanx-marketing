package com.hzltd.module.erplus.ai.dal.dataobject.mas;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * MAS 会话上下文变量 DO
 */
@TableName("ai_mas_session_variable")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasSessionVariableDO extends BaseDO {

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
     * 变量键
     */
    private String varKey;
    /**
     * 变量值
     */
    private String varValue;
    /**
     * 变量类型
     */
    private String varType;

}
