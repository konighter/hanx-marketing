package com.hzltd.module.erplus.ai.service.mas;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSessionDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSessionVariableDO;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskHistoryDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasAgentConfigMapper;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasSessionMapper;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasSessionVariableMapper;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasTaskHistoryMapper;
import com.hzltd.module.erplus.ai.masv0.framework.agent.MasRole;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasContext;
import com.hzltd.module.erplus.ai.masv0.framework.execution.MasTask;
import com.hzltd.module.erplus.ai.masv0.framework.state.MasState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hzltd.framework.common.pojo.PageResult;
import com.hzltd.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.hzltd.module.erplus.ai.controller.admin.mas.vo.MasSessionPageReqVO;

import java.util.List;
import java.util.Map;

/**
 * MAS 持久化服务实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MasPersistenceServiceImpl implements MasPersistenceService {

    private final MasSessionMapper sessionMapper;
    private final MasTaskHistoryMapper taskHistoryMapper;
    private final MasSessionVariableMapper variableMapper;
    private final MasAgentConfigMapper agentConfigMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateSession(String sessionId, String goal, MasState state) {
        MasSessionDO session = sessionMapper.selectBySessionId(sessionId);
        if (session == null) {
            session = MasSessionDO.builder()
                    .sessionId(sessionId)
                    .goal(goal)
                    .status(state.name())
                    .build();
            sessionMapper.insert(session);
        } else {
            session.setStatus(state.name());
            sessionMapper.updateById(session);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordTaskHistory(String sessionId, MasTask task, String result, String status, Long executionTime) {
        MasTaskHistoryDO history = MasTaskHistoryDO.builder()
                .sessionId(sessionId)
                .taskId(task.getTaskId())
                .name(task.getName())
                .role(task.getRoleRequited())
                .prompt(task.getPrompt())
                .result(result)
                .status(status)
                .isInternal(task.isInternal())
                .executionTime(executionTime)
                .build();
        taskHistoryMapper.insert(history);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveContextVariables(String sessionId, MasContext context) {
        Map<String, Object> vars = context.getVariables();
        vars.forEach((key, value) -> {
            MasSessionVariableDO variable = MasSessionVariableDO.builder()
                    .sessionId(sessionId)
                    .varKey(key)
                    .varValue(value != null ? value.toString() : null)
                    .varType(value != null ? value.getClass().getSimpleName() : "String")
                    .build();
            // TODO: 这里可以用 upsert 逻辑，或者先删后加
            variableMapper.insert(variable);
        });
    }

    @Override
    public MasAgentConfigDO getAgentConfig(MasRole role) {
        return agentConfigMapper.selectOne(MasAgentConfigDO::getRoleCode, role.name());
    }

    @Override
    public void loadContextVariables(String sessionId, MasContext context) {
        List<MasSessionVariableDO> variables = variableMapper.selectListBySessionId(sessionId);
        variables.forEach(v -> context.setVariable(v.getVarKey(), v.getVarValue()));
    }

    @Override
    public PageResult<MasSessionDO> getSessionPage(MasSessionPageReqVO pageReqVO) {
        return sessionMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<MasSessionDO>()
                .likeIfPresent(MasSessionDO::getGoal, pageReqVO.getGoal())
                .eqIfPresent(MasSessionDO::getStatus, pageReqVO.getStatus())
                .orderByDesc(MasSessionDO::getId));
    }

    @Override
    public MasSessionDO getSession(String sessionId) {
        return sessionMapper.selectBySessionId(sessionId);
    }

    @Override
    public List<MasTaskHistoryDO> getTaskHistoryList(String sessionId) {
        return taskHistoryMapper.selectListBySessionId(sessionId);
    }

    @Override
    public List<com.hzltd.module.erplus.ai.dal.dataobject.mas.MasEventLogDO> getEventLogList(String sessionId) {
        return ((com.hzltd.module.erplus.ai.dal.mysql.mas.MasEventLogMapper)
                com.hzltd.framework.common.util.spring.SpringUtils.getBean(com.hzltd.module.erplus.ai.dal.mysql.mas.MasEventLogMapper.class))
                .selectList(new LambdaQueryWrapperX<com.hzltd.module.erplus.ai.dal.dataobject.mas.MasEventLogDO>()
                        .eq(com.hzltd.module.erplus.ai.dal.dataobject.mas.MasEventLogDO::getSessionId, sessionId)
                        .orderByAsc(com.hzltd.module.erplus.ai.dal.dataobject.mas.MasEventLogDO::getId));
    }
}
