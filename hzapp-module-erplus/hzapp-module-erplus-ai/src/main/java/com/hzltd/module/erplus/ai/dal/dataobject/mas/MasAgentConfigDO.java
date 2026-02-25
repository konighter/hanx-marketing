package com.hzltd.module.erplus.ai.dal.dataobject.mas;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * MAS 智能体角色配置 DO
 */
@TableName("ai_mas_agent_config")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasAgentConfigDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 角色代码 (MANAGER/PM/EXECUTOR/EXPERT/REVIEWER)
     */
    private String roleCode;
    /**
     * 智能体名称
     */
    private String agentName;
    /**
     * 系统提示词
     */
    private String systemPrompt;
    /**
     * 绑定的工具 Bean 名称列表 (JSON 格式)
     */
    private String toolBeans;
    /**
     * 扩展配置 (JSON 格式)
     */
    private String extConfig;

}
