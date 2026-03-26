package com.hzltd.module.erplus.adv.mas.dal.dataobject;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.mybatis.core.dataobject.BaseDO;
import com.hzltd.module.erplus.adv.mas.controller.admin.vo.FlowConfigVO;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * MAS 技能定义表
 * 存储可复用的 Agent 执行策略模板
 */
@TableName("mas_skill_def")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasSkillDefDO extends BaseDO {

    /**
     * 主键 ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 技能唯一编码 (例如: NEW_PRODUCT_AD_SP_STRATEGY)
     */
    private String skillCode;

    /**
     * 技能名称
     */
    private String name;

    /**
     * 技能描述
     */
    private String description;

    /**
     * 技能图标 url
     */
    private String icon;

    /**
     * 适用场景标签，多个用逗号分隔
     */
    private String useCaseTags;

    /**
     * 策略指导书 (LongText)，提供给 PlannerAgent 的主要 Prompt 规则
     */
    private String strategyInstruction;

    /**
     * 该技能允许使用的工具集集合 (JSON Array)
     */
    private String requiredTools;

    /**
     * 技能参数定义 schema (JSON)，供前端动态渲染初始化表单
     */
    private String paramSchema;

    /**
     * 流程配置信息
     */
    private String flowConfig;


    public List<FlowConfigVO> getFlowConfigs() {
        if (StringUtils.isEmpty(this.flowConfig)) {
            return Collections.emptyList();
        }

        return JsonUtils.parseArray(this.flowConfig, FlowConfigVO.class);
    }


}
