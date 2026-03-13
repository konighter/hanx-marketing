package com.hzltd.module.erplus.adv.service.mas;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.MasSkillListVO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.StrategyInstructionVO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasTaskSkillRelDO;
import com.hzltd.module.erplus.adv.dal.mysql.mas.MasSkillDefMapper;
import com.hzltd.module.erplus.adv.dal.mysql.mas.MasTaskSkillRelMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MasSkillServiceImpl implements MasSkillService {

    @Autowired
    private MasSkillDefMapper masSkillDefMapper;

    @Autowired
    private MasTaskSkillRelMapper masTaskSkillRelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<MasSkillListVO> getAllSkills() {
        List<MasSkillDefDO> pos = masSkillDefMapper.selectList();
        List<MasSkillListVO> vos = new ArrayList<>();
        for (MasSkillDefDO po : pos) {
            MasSkillListVO vo = new MasSkillListVO();
            BeanUtils.copyProperties(po, vo);
            try {
                if (po.getStrategyInstruction() != null && po.getStrategyInstruction().trim().startsWith("{")) {
                    StrategyInstructionVO structuredVO = objectMapper.readValue(po.getStrategyInstruction(), StrategyInstructionVO.class);
                    // 补充完整的 markdown 到 strategy 字段 (实际可能来源于库里另一个字段，这里模拟放入原始 JSON)
                    structuredVO.setStrategy(po.getStrategyInstruction());
                    vo.setStrategyInstruction(structuredVO);
                } else if (po.getStrategyInstruction() != null) {
                    // 兼容旧的普通文本格式
                    StrategyInstructionVO fallbackVO = new StrategyInstructionVO();
                    StrategyInstructionVO.Summary summary = new StrategyInstructionVO.Summary();
                    summary.setDescription(po.getStrategyInstruction());
                    fallbackVO.setSummary(summary);
                    fallbackVO.setStrategy(po.getStrategyInstruction());
                    vo.setStrategyInstruction(fallbackVO);
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
        // Mocking the creation of mas_task_job id since it usually requires interacting with mas-runtime APIs
        Long mockedTaskId = System.currentTimeMillis(); 
        log.info("Mocked MasTask Creation for TargetBizId {}: taskId={}", targetBizId, mockedTaskId);

        MasTaskSkillRelDO relation = MasTaskSkillRelDO.builder()
                .taskId(mockedTaskId)
                .skillCode(skillCode)
                .targetBizId(targetBizId)
                .configParams(configParams)
                .currentStage("Phase 1: Initializing")
                .build();
        
        masTaskSkillRelMapper.insert(relation);
        return mockedTaskId;
    }

    @Override
    public MasTaskSkillRelDO getSkillRelByTaskId(Long taskId) {
        return masTaskSkillRelMapper.selectByTaskId(taskId);
    }
}
