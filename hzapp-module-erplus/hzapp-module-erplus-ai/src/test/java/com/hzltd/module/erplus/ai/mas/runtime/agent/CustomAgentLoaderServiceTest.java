package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasAgentConfigDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasAgentConfigMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomAgentLoaderServiceTest {

    @Mock
    private MasAgentConfigMapper agentConfigMapper;

    @Mock
    private AdkAgentFactory adkAgentFactory;

    @Mock
    private DynamicAdkAgent mockAgent;

    @InjectMocks
    private CustomAgentLoaderService loaderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitAgents_Success() {
        // Arrange
        MasAgentConfigDO configDO = new MasAgentConfigDO();
        configDO.setRoleCode("test_role");
        
        when(agentConfigMapper.selectList(any())).thenReturn(Collections.singletonList(configDO));
        when(adkAgentFactory.createFromConfig(configDO)).thenReturn(mockAgent);

        // Act
        loaderService.initAgents();

        // Assert
        DynamicAdkAgent loadedAgent = loaderService.getAgentByRole("test_role");
        assertNotNull(loadedAgent);
        assertEquals(mockAgent, loadedAgent);
        verify(adkAgentFactory, times(1)).createFromConfig(configDO);
    }

    @Test
    void testReloadAgent_Success() {
        // Arrange
        MasAgentConfigDO configDO = new MasAgentConfigDO();
        configDO.setRoleCode("reload_role");

        when(agentConfigMapper.selectOne(any())).thenReturn(configDO);
        when(adkAgentFactory.createFromConfig(configDO)).thenReturn(mockAgent);

        // Act
        loaderService.reloadAgent("reload_role");

        // Assert
        DynamicAdkAgent reloadedAgent = loaderService.getAgentByRole("reload_role");
        assertNotNull(reloadedAgent);
        assertEquals(mockAgent, reloadedAgent);
    }
    
    @Test
    void testReloadAgent_NotFound() {
        // Arrange
        when(agentConfigMapper.selectOne(any())).thenReturn(null);
                
        // Populate registry with dummy first
        MasAgentConfigDO configDO = new MasAgentConfigDO();
        configDO.setRoleCode("removed_role");
        when(agentConfigMapper.selectList(any())).thenReturn(Collections.singletonList(configDO));
        when(adkAgentFactory.createFromConfig(configDO)).thenReturn(mockAgent);
        loaderService.initAgents();
        assertNotNull(loaderService.getAgentByRole("removed_role"));

        // Act: reload a missing config should remove it from registry
        loaderService.reloadAgent("removed_role");

        // Assert
        DynamicAdkAgent reloadedAgent = loaderService.getAgentByRole("removed_role");
        assertNull(reloadedAgent);
    }
}
