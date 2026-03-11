package com.hzltd.module.erplus.ai.masv0.framework.execution;

import lombok.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MAS 执行上下文，持有会话状态和记忆
 */
@Data
public class MasContext {

    private final String sessionId;
    
    /**
     * 全局动态参数
     */
    private final Map<String, Object> variables = new ConcurrentHashMap<>();
    
    /**
     * 任务历史记录 (短期记忆)
     */
    private final Map<String, Object> executionResults = new ConcurrentHashMap<>();

    public MasContext(String sessionId) {
        this.sessionId = sessionId;
    }

    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getVariable(String key) {
        return (T) variables.get(key);
    }
}
