package com.hzltd.module.erplus.adv.service.mas;

import com.google.common.base.Joiner;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.framework.tenant.core.context.TenantContextHolder;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.MasSkillListVO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.StrategyInstructionVO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasTaskSkillRelDO;
import com.hzltd.module.erplus.adv.dal.mysql.mas.MasSkillDefMapper;
import com.hzltd.module.erplus.adv.dal.mysql.mas.MasTaskSkillRelMapper;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSkillInstanceRelDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasSkillInstanceRelMapper;
import com.hzltd.module.erplus.ai.workflow.MasWorkflowManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.ADV_SKILL_NOT_EXISTS;

@Service
@Slf4j
public class MasSkillServiceImpl implements MasSkillService {

    @Resource
    private MasSkillDefMapper masSkillDefMapper;

    @Resource
    private MasSkillInstanceRelMapper masSkillInstanceRelMapper;

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
                    // 补充完整的 markdown 到 strategy 字段 (实际可能来源于库里另一个字段，这里模拟放入原始 JSON)
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

        String skillBusinessKey = Joiner.on(":").join(TenantContextHolder.getTenantId(),skillCode,targetBizId);

        String workflow = strategicPlannerService.createWorkflowFromSkill(skillBusinessKey, skillDefDO, configParams);

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
    public List<MasSkillInstanceRelDO> getSkillRelByTaskId(String skillCode) {
        return masSkillInstanceRelMapper.selectBySkill(skillCode);
    }
}
