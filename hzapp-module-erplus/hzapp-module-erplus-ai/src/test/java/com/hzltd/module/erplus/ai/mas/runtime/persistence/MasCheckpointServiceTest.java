package com.hzltd.module.erplus.ai.mas.runtime.persistence;

import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskHistoryDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasTaskHistoryMapper;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MasCheckpointServiceTest {

    @Test
    void testSaveCheckpoint() {
        // Arrange
        MasTaskHistoryMapper mockMapper = mock(MasTaskHistoryMapper.class);
        MasCheckpointService service = new MasCheckpointService(mockMapper);

        LoopMemory mockMemory = mock(LoopMemory.class);
        Map<String, Object> memoryMap = new HashMap<>();
        memoryMap.put("key1", "value1");
        when(mockMemory.asMap()).thenReturn(memoryMap);

        // Act
        service.saveCheckpoint("S1", "T1", "AGENT_R", "P1", "RES1", "SUCCESS", mockMemory, 100L);

        // Assert
        ArgumentCaptor<MasTaskHistoryDO> captor = ArgumentCaptor.forClass(MasTaskHistoryDO.class);
        verify(mockMapper).insert(captor.capture());
        
        MasTaskHistoryDO history = captor.getValue();
        assertEquals("S1", history.getSessionId());
        assertEquals("T1", history.getTaskId());
        assertEquals("SUCCESS", history.getStatus());
        assertEquals("RES1", history.getResult());
        assertEquals(100L, history.getExecutionTime());
    }
}
