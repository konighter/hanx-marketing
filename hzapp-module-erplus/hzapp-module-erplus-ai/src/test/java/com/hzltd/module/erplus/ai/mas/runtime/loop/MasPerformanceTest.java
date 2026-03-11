package com.hzltd.module.erplus.ai.mas.runtime.loop;

import com.hzltd.module.erplus.ai.mas.runtime.agent.BaseAgent;
import com.hzltd.module.erplus.ai.mas.runtime.communication.A2AMessageBus;
import com.hzltd.module.erplus.ai.mas.runtime.communication.AgentMessage;
import com.hzltd.module.erplus.ai.mas.runtime.communication.MasEventLogService;
import com.hzltd.module.erplus.ai.mas.runtime.memory.GlobalSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.MasMemoryService;
import com.hzltd.module.erplus.ai.mas.runtime.persistence.MasCheckpointService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Performance stress test for MAS runtime components.
 */
class MasPerformanceTest {

    @Test
    void testHighConcurrencyExecution() throws InterruptedException, ExecutionException {
        // 1. Setup mocks
        MasCheckpointService checkpointService = mock(MasCheckpointService.class);
        MasMemoryService memoryService = mock(MasMemoryService.class);
        MasEventLogService eventLogService = mock(MasEventLogService.class);
        GlobalSessionMemory sessionMemory = mock(GlobalSessionMemory.class);
        when(sessionMemory.getSessionId()).thenReturn("stress-test-session");

        // 2. Build a large graph (50 parallel nodes)
        List<GraphNode> nodes = new ArrayList<>();
        int nodeCount = 50;
        CountDownLatch latch = new CountDownLatch(nodeCount);
        AtomicInteger successCount = new AtomicInteger(0);

        ExecutorService concurrentPool = Executors.newFixedThreadPool(20);
        List<Future<String>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (int i = 0; i < nodeCount; i++) {
            final int index = i;
            BaseAgent dummyAgent = new BaseAgent() {
                @Override public String getRoleName() { return "WorkerAgent"; }
                @Override public void onMessage(AgentMessage message) {}
                @Override public String getInstruction() { return "Do work " + index; }
                @Override public String execute(LoopMemory memory) {
                    try {
                        Thread.sleep(100); 
                    } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                    successCount.incrementAndGet();
                    latch.countDown();
                    return "Result for " + getInstruction();
                }
            };

            GraphNode node = new GraphNode("node-" + i, dummyAgent);
            nodes.add(node);

            AgentLoopRunner runner = new AgentLoopRunner(
                node, sessionMemory, checkpointService, memoryService, eventLogService
            );
            futures.add(concurrentPool.submit(runner));
        }

        // 4. Verification
        boolean completed = latch.await(15, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        assertTrue(completed, "Execution timed out! Only " + successCount.get() + " nodes completed.");
        assertEquals(nodeCount, successCount.get());
        
        System.out.println("Stress Test: " + nodeCount + " nodes completed in " + (endTime - startTime) + "ms");
        
        concurrentPool.shutdown();
    }

    @Test
    void testRetryLogicUnderLoad() throws InterruptedException {
        // Setup mocks
        MasCheckpointService checkpointService = mock(MasCheckpointService.class);
        MasMemoryService memoryService = mock(MasMemoryService.class);
        GlobalSessionMemory sessionMemory = mock(GlobalSessionMemory.class);
        when(sessionMemory.getSessionId()).thenReturn("retry-load-session");

        AtomicInteger attemptCount = new AtomicInteger(0);
        BaseAgent failingAgent = new BaseAgent() {
            @Override public String getRoleName() { return "FailureAgent"; }
            @Override public void onMessage(AgentMessage message) {}
            @Override public String getInstruction() { return "Retry task"; }
            @Override public String execute(LoopMemory memory) {
                if (attemptCount.incrementAndGet() < 3) {
                    throw new RuntimeException("Transient Failure");
                }
                return "Success on attempt 3";
            }
        };

        GraphNode node = new GraphNode("retry-node", failingAgent);
        node.setRetryPolicy(new RetryPolicy(3, 50, 2.0, 500));

        AgentLoopRunner runner = new AgentLoopRunner(
            node, sessionMemory, checkpointService, memoryService, null
        );

        long start = System.currentTimeMillis();
        try {
            String result = runner.call();
            assertEquals("Success on attempt 3", result);
            assertEquals(3, attemptCount.get());
        } catch (Exception e) {
            fail("Runner should have succeeded on retry: " + e.getMessage());
        }
        long duration = System.currentTimeMillis() - start;
        
        // Backoff: 50ms + 100ms = 150ms total delay
        assertTrue(duration >= 150, "Retry backoff not respected: " + duration + "ms");
        System.out.println("Retry Test: Success after " + attemptCount.get() + " attempts in " + duration + "ms");
    }
}
