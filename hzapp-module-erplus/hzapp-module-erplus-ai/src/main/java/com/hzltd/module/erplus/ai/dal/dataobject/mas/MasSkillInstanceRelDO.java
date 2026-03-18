package com.hzltd.module.erplus.ai.dal.dataobject.mas;
 
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;
 
/**
 * MAS Skill 实例关联 DO
 * 用于记录 Skill (如广告监控/策略) 与具体业务对象 (如 ASIN) 的订阅/激活关系及其配置
 */
@TableName("mas_skill_instance_rel")
@KeySequence("mas_skill_instance_rel_seq") // 只有 Oracle、PostgreSQL、Kingbase、DB2、H2 需要，MySQL 可以不用
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasSkillInstanceRelDO extends BaseDO {
 
    /**
     * 主键
     */
    @TableId
    private Long id;
 
    /**
     * Skill 代码
     * 例如：AD_MONITOR, AD_STRATEGY
     */
    private String skillCode;
 
    /**
     * 目标业务 ID
     * 例如：ASIN, CampaignId
     */
    private String targetBizId;
 
    /**
     * 目标业务类型
     * 例如：AMZ_ASIN, AMZ_CAMPAIGN
     */
    private String targetBizType;
 
    /**
     * 配置参数 (JSON 格式)
     * 存储该实例特有的运行参数，如 monitorInterval
     */
    private String configParams;
 
    /**
     * 当前阶段
     * 记录实例所处的业务阶段
     */
    private String currentStage;
 
    /**
     * 工作流配置 (JSON 格式)
     * 存储关联的工作流定义或实例 ID 等信息
     */
    private String workflowConfig;
 
}
