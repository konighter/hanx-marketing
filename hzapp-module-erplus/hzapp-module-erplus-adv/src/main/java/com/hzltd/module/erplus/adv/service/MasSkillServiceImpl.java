package com.hzltd.module.erplus.adv.service;

import com.google.common.base.Joiner;
import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.framework.tenant.core.context.TenantContextHolder;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.MasSkillInstanceMessageVO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.MasSkillInstanceVO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.MasSkillListVO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.StrategyInstructionVO;
import com.hzltd.module.erplus.adv.convert.mas.MasSkillConvert;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import com.hzltd.module.erplus.adv.dal.mysql.mas.MasSkillDefMapper;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSkillInstanceLogDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSkillInstanceRelDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasSkillInstanceLogMapper;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasSkillInstanceRelMapper;
import com.hzltd.module.erplus.ai.workflow.MasWorkflowManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.ADV_SKILL_NOT_EXISTS;

/**
 * 策略技能 Service 实现类
 */
@Service
@Slf4j
public class MasSkillServiceImpl implements MasSkillService {

    @Resource
    private MasSkillDefMapper masSkillDefMapper;

    @Resource
    private MasSkillInstanceRelMapper masSkillInstanceRelMapper;

    @Resource
    private MasSkillInstanceLogMapper skillInstanceLogMapper;

    @Resource
    private MasSkillAgenticService strategicPlannerService;

    @Resource
    private MasWorkflowManager masWorkflowManager;

    @Override
    public List<MasSkillListVO> getAllSkills() {
        List<MasSkillDefDO> pos = masSkillDefMapper.selectList();
        List<MasSkillListVO> vos = new ArrayList<>();
        for (MasSkillDefDO po : pos) {
            MasSkillListVO vo = new MasSkillListVO();
            BeanUtils.copyProperties(po, vo);
            try {
                if (StringUtils.isNotEmpty(po.getStrategyInstruction())) {
                    StrategyInstructionVO structuredVO = JsonUtils.parseObject(po.getStrategyInstruction(), StrategyInstructionVO.class);
                    vo.setStrategyInstruction(structuredVO);
                }
            } catch (Exception e) {
                log.error("Failed to parse strategyInstruction for skill {}", po.getSkillCode(), e);
            }
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public MasSkillDefDO getSkillByCode(String skillCode) {
        return masSkillDefMapper.selectBySkillCode(skillCode);
    }

    @Override
    public Long activateSkillForAsin(String skillCode, String targetBizId, String configParams) {
        MasSkillDefDO skillDefDO = masSkillDefMapper.selectBySkillCode(skillCode);
        if (skillDefDO == null) {
            throw exception(ADV_SKILL_NOT_EXISTS);
        }

        String skillBusinessKey = Joiner.on(":").join(TenantContextHolder.getTenantId(), skillCode, targetBizId);
        String workflow = strategicPlannerService.createWorkflowFromSkill(skillBusinessKey, targetBizId, skillDefDO, configParams);

        MasSkillInstanceRelDO relation = MasSkillInstanceRelDO.builder()
                .skillCode(skillCode)
                .targetBizId(targetBizId)
                .workflowConfig(workflow)
                .configParams(configParams)
                .build();

        masSkillInstanceRelMapper.insert(relation);
        return relation.getId();
    }

    @Override
    public PageResult<MasSkillInstanceVO> getSkillInstancePage(String skillCode, String targetBizId, PageParam pageParam) {
        PageResult<MasSkillInstanceRelDO> pageResult = masSkillInstanceRelMapper.selectPage(pageParam, new LambdaQueryWrapperX<MasSkillInstanceRelDO>()
                .eqIfPresent(MasSkillInstanceRelDO::getSkillCode, skillCode)
                .eqIfPresent(MasSkillInstanceRelDO::getTargetBizId, targetBizId));
        
        PageResult<MasSkillInstanceVO> result = MasSkillConvert.INSTANCE.convertPage(pageResult);
        
        // 补充详情信息
        if (result != null && result.getList() != null) {
            for (MasSkillInstanceVO vo : result.getList()) {
                // 1. 补充技能名称
                MasSkillDefDO skillDef = masSkillDefMapper.selectBySkillCode(vo.getSkillCode());
                if (skillDef != null) {
                    vo.setSkillName(skillDef.getName());
                }
                
                // 2. 状态兜底 (由于 DO 暂未存储状态，此处先默认为运行中)
                if (StringUtils.isEmpty(vo.getStatus())) {
                    vo.setStatus("运行中");
                }
                
                // 3. 最新进展兜底
                if (StringUtils.isEmpty(vo.getLatestProgress())) {
                    vo.setLatestProgress("正在进行智能监测与分析，实时优化中...");
                }
                
                // 4. 模拟 ProcessInstanceId (如果为空)
                if (StringUtils.isEmpty(vo.getProcessInstanceId())) {
                    vo.setProcessInstanceId("mock_proc_" + vo.getId());
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MasSkillInstanceLogDO> getSkillInstanceLogs(Long instanceId) {
        return skillInstanceLogMapper.selectByInstanceId(instanceId);
    }

    @Override
    public List<MasSkillInstanceMessageVO> getSkillInstanceMessages(String processInstanceId) {
        // TODO: 使用 ADK 的 DatabaseSessionService 获取真实消息
        List<MasSkillInstanceMessageVO> list = new ArrayList<>();
        
        MasSkillInstanceMessageVO msg1 = new MasSkillInstanceMessageVO();
        msg1.setRole("thought");
        msg1.setContent("正在分析商品流量数据，发现转化率低于平均水平。建议优化关键词布局。");
        msg1.setCreateTime(LocalDateTime.now().minusMinutes(10));
        list.add(msg1);

        MasSkillInstanceMessageVO msg2 = new MasSkillInstanceMessageVO();
        msg2.setRole("assistant");
        msg2.setContent("我已经分析了您的商品。根据目前的广告表现，我建议将 'running shoes' 的竞价提高 10%。您同意吗？");
        msg2.setCreateTime(LocalDateTime.now().minusMinutes(5));
        list.add(msg2);

        MasSkillInstanceMessageVO msg3 = new MasSkillInstanceMessageVO();
        msg3.setRole("user");
        msg3.setContent("同意，请执行。");
        msg3.setCreateTime(LocalDateTime.now().minusMinutes(2));
        list.add(msg3);

        return list;
    }

    @Override
    public void logSkillInstanceEvent(Long instanceId, String type, String title, String content) {
        MasSkillInstanceLogDO logDO = new MasSkillInstanceLogDO();
        logDO.setInstanceId(instanceId);
        logDO.setType(type);
        logDO.setTitle(title);
        logDO.setContent(content);
        skillInstanceLogMapper.insert(logDO);
    }

    @Override
    public List<MasSkillInstanceRelDO> getSkillRelByTaskId(String skillCode) {
        return masSkillInstanceRelMapper.selectBySkill(skillCode);
    }
}
