package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasAgentConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service responsible for loading and managing DynamicAdkAgents from the database.
 * This acts as the runtime registry for all configured agents.
 */
@Slf4j
@Service
public class CustomAgentLoaderService {

    private final MasAgentConfigMapper agentConfigMapper;
    private final AdkAgentFactory adkAgentFactory;
    
    // Registry holding instantiated agents by their roleCode
    private final Map<String, DynamicAdkAgent> agentRegistry = new ConcurrentHashMap<>();

    public CustomAgentLoaderService(MasAgentConfigMapper agentConfigMapper, AdkAgentFactory adkAgentFactory) {
        this.agentConfigMapper = agentConfigMapper;
        this.adkAgentFactory = adkAgentFactory;
    }

    /**
     * Loads all active agent configurations from the database and initializes them.
     */
    @PostConstruct
    public void initAgents() {
        log.info("Loading custom agents from database...");
        LambdaQueryWrapper<MasAgentConfigDO> query = new LambdaQueryWrapper<>();
        // Assuming all records in this table should be loaded.
        // query.eq(MasAgentConfigDO::getStatus, 1); 
        
        List<MasAgentConfigDO> activeConfigs = agentConfigMapper.selectList(query);
        
        for (MasAgentConfigDO configDO : activeConfigs) {
            try {
                DynamicAdkAgent agent = adkAgentFactory.createFromConfig(configDO);
                agentRegistry.put(configDO.getRoleCode(), agent);
                log.info("Successfully loaded agent role: {}", configDO.getRoleCode());
            } catch (Exception e) {
                log.error("Failed to load agent role: {}", configDO.getRoleCode(), e);
            }
        }
        log.info("Finished loading {} agents.", agentRegistry.size());
    }

    /**
     * Retrieves an initialized agent by its role code.
     * @param roleCode The unique role code of the agent.
     * @return The agent instance, or null if not found.
     */
    public DynamicAdkAgent getAgentByRole(String roleCode) {
        return agentRegistry.get(roleCode);
    }
    
    /**
     * Reloads a specific agent configuration from the database at runtime.
     * @param roleCode The unique role code of the agent to reload.
     */
    public void reloadAgent(String roleCode) {
        LambdaQueryWrapper<MasAgentConfigDO> query = new LambdaQueryWrapper<>();
        query.eq(MasAgentConfigDO::getRoleCode, roleCode);
             
        MasAgentConfigDO configDO = agentConfigMapper.selectOne(query);
        if (configDO != null) {
            try {
                DynamicAdkAgent agent = adkAgentFactory.createFromConfig(configDO);
                agentRegistry.put(roleCode, agent);
                log.info("Successfully reloaded agent role: {}", roleCode);
            } catch (Exception e) {
                log.error("Failed to reload agent role: {}", roleCode, e);
            }
        } else {
            log.warn("Cannot reload agent {}: Config not found or inactive.", roleCode);
            agentRegistry.remove(roleCode); // Remove if deactivated
        }
    }
}
