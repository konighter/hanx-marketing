package com.hzltd.module.erplus.adv.dal.dataobject.mas;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * MAS 任务与技能绑定关系表
 * 记录每个具体的大型长周期任务使用了哪个技能池，并应用到了哪个具体业务对象上
 */
@TableName("mas_task_skill_rel")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasTaskSkillRelDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联的 mas_task_job 的 ID
     */
    private Long taskId;

    /**
     * 应用的技能编码 (关联 mas_skill_def.skill_code)
     */
    private String skillCode;

    /**
     * 目标业务对象的唯一标识 (如 商品 ASIN 编号)
     */
    private String targetBizId;

    /**
     * 目标业务对象的名称 (如 商品 Title)
     */
    private String targetBizName;

    /**
     * 用户配置的运行参数 (JSON 格式)，如：期待 ROAS、预算、竞价限制等
     */
    private String configParams;

    /**
     * 该技能执行在该任务中的当前进度或状态文本 (供 Copilot 展板使用)
     */
    private String currentStage;
}
