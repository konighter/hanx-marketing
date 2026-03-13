package com.hzltd.module.erplus.adv.service.mas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.MasSkillListVO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.StrategyInstructionVO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasTaskSkillRelDO;
import com.hzltd.module.erplus.adv.dal.mysql.mas.MasSkillDefMapper;
import com.hzltd.module.erplus.adv.dal.mysql.mas.MasTaskSkillRelMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.hzltd.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.hzltd.module.erplus.enums.ErrorCodeConstants.ADV_SKILL_NOT_EXISTS;

@Service
@Slf4j
public class MasSkillServiceImpl implements MasSkillService {

    @Resource
    private MasSkillDefMapper masSkillDefMapper;

    @Resource
    private MasTaskSkillRelMapper masTaskSkillRelMapper;

    @Resource
    private MasStrategicPlannerService strategicPlannerService;



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

        Long taskId = strategicPlannerService.createTaskFromSkill(UUID.randomUUID().toString(), skillDefDO, configParams);

        MasTaskSkillRelDO relation = MasTaskSkillRelDO.builder()
                .taskId(taskId)
                .skillCode(skillCode)
                .targetBizId(targetBizId)
                .configParams(configParams)
                .currentStage("Phase 1: Initializing")
                .build();
        
        masTaskSkillRelMapper.insert(relation);

        return taskId;
    }

    @Override
    public MasTaskSkillRelDO getSkillRelByTaskId(Long taskId) {
        return masTaskSkillRelMapper.selectByTaskId(taskId);
    }
}
