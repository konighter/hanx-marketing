package com.hzltd.module.erplus.ai.service.mas;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSessionDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskHistoryDO;
import com.hzltd.module.erplus.ai.masv0.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasContext;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasTask;
import com.hzltd.module.erplus.ai.masv0.framework.state.MasState;

/**
 * MAS 持久化服务接口
 */
public interface MasPersistenceService {

    /**
     * 保存或更新会话状态
     */
    void saveOrUpdateSession(String sessionId, String goal, MasState state);

    /**
     * 记录任务执行历史
     */
    void recordTaskHistory(String sessionId, MasTask task, String result, String status, Long executionTime);

    /**
     * 保存会话上下文变量
     */
    void saveContextVariables(String sessionId, MasContext context);

    /**
     * 根据角色获取智能体配置
     */
    MasAgentConfigDO getAgentConfig(MasRole role);

    /**
     * 加载会话上下文至 MasContext
     */
    void loadContextVariables(String sessionId, MasContext context);

    /**
     * 分页查询会话记录
     */
    com.hzltd.framework.common.pojo.PageResult<MasSessionDO> getSessionPage(com.hzltd.module.erplus.ai.controller.admin.mas.vo.MasSessionPageReqVO pageReqVO);

    /**
     * 获取指定会话
     */
    MasSessionDO getSession(String sessionId);

    /**
     * 获取指定会话的任务历史
     */
    java.util.List<MasTaskHistoryDO> getTaskHistoryList(String sessionId);

    /**
     * 获取指定会话的执行事件日志
     */
    java.util.List<com.hzltd.module.erplus.ai.dal.dataobject.mas.MasEventLogDO> getEventLogList(String sessionId);
}
