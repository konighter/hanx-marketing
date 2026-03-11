package com.hzltd.module.erplus.ai.mas.runtime.orchestration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.module.erplus.ai.mas.runtime.agent.PlannerAgent;
import com.hzltd.module.erplus.ai.mas.runtime.agent.ReviewerAgent;
import com.hzltd.module.erplus.ai.mas.runtime.communication.A2AMessageBus;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.GlobalSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LocalLoopMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.MasMemoryService;
import com.hzltd.module.erplus.ai.mas.runtime.persistence.MasCheckpointService;
import com.hzltd.module.erplus.ai.mas.runtime.prompt.schema.DagGenerationPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class WorkflowOrchestratorTest {

    @Mock
    private PlannerAgent plannerAgent;
    @Mock
    private ReviewerAgent reviewerAgent;
    @Mock
    private DagParserUtil dagParserUtil;
    @Mock
    private MasMemoryService memoryService;
    @Mock
    private MasCheckpointService checkpointService;
    @Mock
    private MasEventLogService eventLogService;
    @Mock
    private A2AMessageBus messageBus;

    private ObjectMapper objectMapper = new ObjectMapper();

    private WorkflowOrchestrator orchestrator;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        orchestrator = new WorkflowOrchestrator(
                plannerAgent,
                reviewerAgent,
                dagParserUtil,
                memoryService,
                checkpointService,
                eventLogService,
                messageBus,
                objectMapper
        );
    }

    @Test
    public void testExecuteMacroLoopWithDoneStatus() throws Exception {
        GlobalSessionMemory mockMemory = new GlobalSessionMemory("test-session");
        when(memoryService.getSessionMemory(anyString())).thenReturn(mockMemory);
        
        DagGenerationPlan donePlan = DagGenerationPlan.builder()
                .status(DagGenerationPlan.Status.DONE)
                .reasoning("Goal achieved.")
                .nodes(new ArrayList<>())
                .build();
                
        String jsonOutput = objectMapper.writeValueAsString(donePlan);
        when(plannerAgent.execute(any(LocalLoopMemory.class))).thenReturn(jsonOutput);

        orchestrator.executeMacroLoop("test-session", "Test Goal");

        verify(plannerAgent, times(1)).execute(any(LocalLoopMemory.class));
        verify(dagParserUtil, never()).parse(any());
    }

    @Test
    public void testExecuteMacroLoopInProgressThenDone() throws Exception {
        GlobalSessionMemory mockMemory = new GlobalSessionMemory("test-session-2");
        when(memoryService.getSessionMemory(anyString())).thenReturn(mockMemory);
        
        DagGenerationPlan progressPlan = DagGenerationPlan.builder()
                .status(DagGenerationPlan.Status.IN_PROGRESS)
                .reasoning("First phase completed, need next phase.")
                .nodes(new ArrayList<>()) // Returning empty nodes to break loop safely in test
                .build();
                
        DagGenerationPlan donePlan = DagGenerationPlan.builder()
                .status(DagGenerationPlan.Status.DONE)
                .reasoning("Goal achieved.")
                .nodes(new ArrayList<>())
                .build();

        when(plannerAgent.execute(any(LocalLoopMemory.class)))
                .thenReturn(objectMapper.writeValueAsString(progressPlan))
                .thenReturn(objectMapper.writeValueAsString(donePlan));

        // When DagParser returns empty, the loop breaks to prevent infinite spin.
        when(dagParserUtil.parse(any())).thenReturn(new ArrayList<>());
        
        orchestrator.executeMacroLoop("test-session-2", "MultiPhase Goal");

        verify(plannerAgent, times(1)).execute(any(LocalLoopMemory.class));
    }
}
