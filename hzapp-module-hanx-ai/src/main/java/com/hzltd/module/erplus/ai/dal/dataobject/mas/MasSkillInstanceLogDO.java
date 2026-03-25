package com.hzltd.module.erplus.ai.dal.dataobject.mas;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * MAS 技能实例运行日志 DO
 */
@TableName("mas_skill_instance_log")
@KeySequence("mas_skill_instance_log_seq") // 用于 Oracle、PostgreSQL、Kingbase 等有序列的数据库
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasSkillInstanceLogDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;

    /**
     * 关联实例ID
     */
    private Long instanceId;

    /**
     * 日志类型(INFO, SUCCESS, WARNING, ERROR)
     */
    private String type;

    /**
     * 步骤标题
     */
    private String title;

    /**
     * 详细内容
     */
    private String content;

}
