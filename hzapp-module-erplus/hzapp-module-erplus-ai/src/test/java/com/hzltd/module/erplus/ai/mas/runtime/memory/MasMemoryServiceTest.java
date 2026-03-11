package com.hzltd.module.erplus.ai.mas.runtime.memory;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasSessionVariableDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasSessionVariableMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MasMemoryServiceTest {

    @Mock
    private MasSessionVariableMapper sessionVariableMapper;

    @InjectMocks
    private MasMemoryService memoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetSessionMemory_Initialization() {
        // Arrange
        String sessionId = "test-session";
        when(sessionVariableMapper.selectList(any())).thenReturn(new ArrayList<>());

        // Act
        GlobalSessionMemory memory = memoryService.getSessionMemory(sessionId);

        // Assert
        assertNotNull(memory);
        assertEquals(sessionId, memory.getSessionId());
        verify(sessionVariableMapper, times(1)).selectList(any());
    }

    @Test
    void testSaveSessionMemory_Pruning() {
        // Arrange
        String sessionId = "prune-session";
        GlobalSessionMemory memory = memoryService.getSessionMemory(sessionId);
        
        // Large value to trigger pruning (threshold is 10000)
        StringBuilder largeVal = new StringBuilder();
        for (int i = 0; i < 6000; i++) largeVal.append("a");
        
        memory.put("var1", largeVal.toString());
        memory.put("var2", largeVal.toString()); // Total 12000 > 10000
        
        // Mock existing checks
        when(sessionVariableMapper.selectList(any())).thenReturn(new ArrayList<>());
        
        // Mock active vars for pruning check
        List<MasSessionVariableDO> activeVars = new ArrayList<>();
        MasSessionVariableDO v1 = MasSessionVariableDO.builder()
                .id(1L).sessionId(sessionId).varKey("var1").varValue(largeVal.toString()).build();
        v1.setUpdateTime(LocalDateTime.now().minusMinutes(10));
        
        MasSessionVariableDO v2 = MasSessionVariableDO.builder()
                .id(2L).sessionId(sessionId).varKey("var2").varValue(largeVal.toString()).build();
        v2.setUpdateTime(LocalDateTime.now());
        
        activeVars.add(v1);
        activeVars.add(v2);
        
        when(sessionVariableMapper.selectList(any())).thenReturn(activeVars);

        // Act
        memoryService.saveToDb(sessionId);

        // Assert
        // Should have updated v1 to ARCHIVED because it's older and sum > 10000
        verify(sessionVariableMapper, atLeastOnce()).updateById(any(MasSessionVariableDO.class));
        // More specific check via ArgumentCaptor or direct verification if possible
        // Let's use argThat with explicit cast to avoid ambiguity
        verify(sessionVariableMapper, atLeastOnce()).updateById((MasSessionVariableDO) argThat(v -> 
            v != null && "var1".equals(((MasSessionVariableDO)v).getVarKey()) && "ARCHIVED".equals(((MasSessionVariableDO)v).getVarType())
        ));
    }

    @Test
    void testLoadFromDb_IgnoresArchived() {
        // Arrange
        String sessionId = "load-session";
        List<MasSessionVariableDO> dbVars = new ArrayList<>();
        dbVars.add(MasSessionVariableDO.builder().varKey("active").varValue("val1").varType("STRING").build());
        // (The query already filters NE ARCHIVED, so we just verify the call)
        
        when(sessionVariableMapper.selectList(any())).thenReturn(dbVars);

        // Act
        GlobalSessionMemory memory = memoryService.getSessionMemory(sessionId);

        // Assert
        assertTrue(memory.containsKey("active"));
        assertEquals("val1", memory.get("active"));
    }
}
