package com.hzltd.module.erplus.adv.mas.service;

import com.hzltd.framework.common.pojo.PageParam;
import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.module.erplus.adv.mas.controller.admin.vo.MasSkillInstanceMessageVO;
import com.hzltd.module.erplus.adv.mas.controller.admin.vo.MasSkillInstanceVO;
import com.hzltd.module.erplus.adv.mas.controller.admin.vo.MasSkillListVO;
import com.hzltd.module.erplus.adv.mas.dal.dataobject.MasSkillDefDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSkillInstanceLogDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSkillInstanceRelDO;

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
     * 分页查询所有激活的技能实例关联信息 (包含 Flowable 状态)
     */
    PageResult<MasSkillInstanceVO> getSkillInstancePage(String skillCode, String targetBizId, PageParam pageParam);

    /**
     * 获取指定实例的运行日志
     */
    List<MasSkillInstanceLogDO> getSkillInstanceLogs(Long instanceId);

    /**
     * 获取指定实例的详细运行消息 (含 AI 思考和对话)
     */
    List<MasSkillInstanceMessageVO> getSkillInstanceMessages(String processInstanceId);
    
    /**
     * 记录技能实例运行日志
     */
    void logSkillInstanceEvent(Long instanceId, String type, String title, String content);

    /**
     * 根据 TaskID 获取应用的技能关联信息
     */
    List<MasSkillInstanceRelDO> getSkillRelByTaskId(String skillCode);
    
}
