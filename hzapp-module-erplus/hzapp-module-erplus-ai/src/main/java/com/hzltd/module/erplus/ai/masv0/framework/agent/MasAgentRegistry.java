package com.hzltd.module.erplus.ai.masv0.framework.agent;

import java.util.List;
import java.util.Optional;

/**
 * MAS Agent 注册中心接口
 */
public interface MasAgentRegistry {

    void register(MasAgent agent);

    void register(String roleId, MasAgent agent);

    void unregister(String roleId);

    Optional<MasAgent> getAgent(String roleId);

    List<MasAgent> getAllAgents();

    List<MasAgent> getEnabledAgents();

    void loadFromConfig(List<MasRoleConfig> configs);

    void updateConfig(String roleId, MasRoleConfig config);

    Optional<MasRoleConfig> getConfig(String roleId);

    List<MasRoleConfig> getAllConfigs();
}
