package com.hzltd.module.erplus.adv.service.mas;

import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasSkillDefDO;
import com.hzltd.module.erplus.adv.dal.dataobject.mas.MasTaskSkillRelDO;
import com.hzltd.module.erplus.adv.controller.admin.mas.vo.MasSkillListVO;
import java.util.List;

public interface MasSkillService {
    
    /**
     * 获取所有可用的 MAS 技能模板
     */
    List<MasSkillListVO> getAllSkills();
    
    /**
     * 根据技能编码获取技能详情
     */
    MasSkillDefDO getSkillByCode(String skillCode);
    
    /**
     * 激活并绑定技能到一个具体的 ASIN 上
     * @param skillCode 技能编码
     * @param targetBizId 目标对象 ID (如 ASIN)
     * @param configParams JSON 格式的启动参数
     * @return 关联的 TaskID
     */
    Long activateSkillForAsin(String skillCode, String targetBizId, String configParams);

    /**
     * 根据 TaskID 获取应用的技能关联信息
     */
    MasTaskSkillRelDO getSkillRelByTaskId(Long taskId);
    
}
