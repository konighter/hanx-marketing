package com.hzltd.module.erplus.ai.masv0.framework.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class DefaultMasAgentRegistry implements MasAgentRegistry {

    private final Map<String, MasAgent> agentMap = new ConcurrentHashMap<>();
    private final Map<String, MasRoleConfig> configMap = new ConcurrentHashMap<>();
    private final ChatClient.Builder chatClientBuilder;

    public DefaultMasAgentRegistry(ChatClient.Builder chatClientBuilder) {
        this.chatClientBuilder = chatClientBuilder;
        loadDefaults();
    }

    private void loadDefaults() {
        List<MasRoleConfig> defaults = MasRoleConfig.Defaults.all();
        for (MasRoleConfig config : defaults) {
            configMap.put(config.getRoleId(), config);
            MasAgent agent = createAgentFromConfig(config);
            if (agent != null) {
                agentMap.put(config.getRoleId(), agent);
            }
        }
        log.info("Default MasAgentRegistry initialized with {} roles", agentMap.size());
    }

    private MasAgent createAgentFromConfig(MasRoleConfig config) {
        if (config == null || !config.isEnabled()) {
            return null;
        }
        return new ConfiguredMasAgent(config, chatClientBuilder);
    }

    @Override
    public void register(MasAgent agent) {
        if (agent != null && agent.getRole() != null) {
            register(agent.getRole().name(), agent);
        }
    }

    @Override
    public void register(String roleId, MasAgent agent) {
        if (roleId != null && agent != null) {
            agentMap.put(roleId, agent);
            log.info("Registered agent: {} for role: {}", agent.getName(), roleId);
        }
    }

    @Override
    public void unregister(String roleId) {
        MasAgent removed = agentMap.remove(roleId);
        if (removed != null) {
            log.info("Unregistered agent: {} from role: {}", removed.getName(), roleId);
        }
    }

    @Override
    public Optional<MasAgent> getAgent(String roleId) {
        return Optional.ofNullable(agentMap.get(roleId));
    }

    @Override
    public List<MasAgent> getAllAgents() {
        return List.copyOf(agentMap.values());
    }

    @Override
    public List<MasAgent> getEnabledAgents() {
        return agentMap.values().stream()
                .filter(a -> {
                    MasRoleConfig config = configMap.get(a.getRole() != null ? a.getRole().name() : null);
                    return config == null || config.isEnabled();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void loadFromConfig(List<MasRoleConfig> configs) {
        if (configs == null) return;
        for (MasRoleConfig config : configs) {
            if (config != null && config.getRoleId() != null) {
                configMap.put(config.getRoleId(), config);
                if (config.isEnabled()) {
                    MasAgent agent = createAgentFromConfig(config);
                    if (agent != null) {
                        agentMap.put(config.getRoleId(), agent);
                    }
                } else {
                    agentMap.remove(config.getRoleId());
                }
            }
        }
        log.info("Loaded {} role configurations", configs.size());
    }

    @Override
    public void updateConfig(String roleId, MasRoleConfig config) {
        if (roleId == null || config == null) return;
        configMap.put(roleId, config);
        if (config.isEnabled()) {
            MasAgent agent = createAgentFromConfig(config);
            if (agent != null) {
                agentMap.put(roleId, agent);
            }
        } else {
            agentMap.remove(roleId);
        }
        log.info("Updated config for role: {}", roleId);
    }

    @Override
    public Optional<MasRoleConfig> getConfig(String roleId) {
        return Optional.ofNullable(configMap.get(roleId));
    }

    @Override
    public List<MasRoleConfig> getAllConfigs() {
        return List.copyOf(configMap.values());
    }
}
