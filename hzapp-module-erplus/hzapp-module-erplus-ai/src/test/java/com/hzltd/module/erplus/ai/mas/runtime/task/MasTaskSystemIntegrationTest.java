package com.hzltd.module.erplus.ai.mas.runtime.task;

import com.hzltd.module.erplus.ai.BaseDbUnitTest;
import com.hzltd.module.erplus.ai.dal.dataobject.mas.MasTaskDO;
import com.hzltd.module.erplus.ai.dal.mysql.mas.MasTaskMapper;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskStatusEnum;
import com.hzltd.module.erplus.ai.mas.runtime.task.enums.MasTaskTypeEnum;
import com.hzltd.module.erplus.ai.mas.runtime.orchestration.WorkflowOrchestrator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@Import({MasTaskServiceImpl.class, MasTaskJob.class})
public class MasTaskSystemIntegrationTest extends BaseDbUnitTest {

    @Autowired
    private MasTaskService taskService;

    @Autowired
    private MasTaskJob taskJob;

    @MockBean
    private WorkflowOrchestrator workflowOrchestrator;

    private String sessionId;

    @BeforeEach
    public void setup() {
        sessionId = UUID.randomUUID().toString();
        // Mock the orchestrator to do nothing (simulation)
        doNothing().when(workflowOrchestrator).executeMacroLoop(anyString(), anyString());
    }

    @Test
    public void testParallelTaskWorkflow() throws InterruptedException {
        // 1. Create Parent (PARALLEL)
        MasTaskDO parent = MasTaskDO.builder()
                .sessionId(sessionId)
                .name("Root Task")
                .taskType(MasTaskTypeEnum.PARALLEL.getType())
                .status(MasTaskStatusEnum.PENDING.getStatus())
                .build();
        taskService.saveTask(parent);

        // 2. Create 2 Children (LEAF)
        MasTaskDO child1 = MasTaskDO.builder()
                .sessionId(sessionId)
                .parentId(parent.getId())
                .name("Sub Task 1")
                .taskType(MasTaskTypeEnum.LEAF.getType())
                .status(MasTaskStatusEnum.PENDING.getStatus())
                .inputData("Goal 1")
                .build();
        taskService.saveTask(child1);

        MasTaskDO child2 = MasTaskDO.builder()
                .sessionId(sessionId)
                .parentId(parent.getId())
                .name("Sub Task 2")
                .taskType(MasTaskTypeEnum.LEAF.getType())
                .status(MasTaskStatusEnum.PENDING.getStatus())
                .inputData("Goal 2")
                .build();
        taskService.saveTask(child2);

        // 3. Trigger Job to start execution
        taskJob.scheduleTasks();

        // 4. Verify children are RUNNING (in separated threads) or have finished
        // Wait a bit for the threads in MasTaskJob to finish their mock execution
        Thread.sleep(1000);

        // Check children status
        MasTaskDO c1 = taskService.getTask(child1.getId());
        MasTaskDO c2 = taskService.getTask(child2.getId());
        assertEquals(MasTaskStatusEnum.SUCCESS.getStatus(), c1.getStatus());
        assertEquals(MasTaskStatusEnum.SUCCESS.getStatus(), c2.getStatus());

        // 5. Verify Parent is now REVIEW_REQUIRED
        MasTaskDO p = taskService.getTask(parent.getId());
        assertEquals(MasTaskStatusEnum.REVIEW_REQUIRED.getStatus(), p.getStatus());
        System.out.println("Parent Aggregated Result:\n" + p.getOutputData());
    }
}
