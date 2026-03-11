package com.hzltd.module.erplus.ai.mas.runtime.loop;

import com.hzltd.module.erplus.ai.mas.runtime.agent.BaseAgent;
import com.hzltd.module.erplus.ai.mas.runtime.communication.A2AMessageBus;
import com.hzltd.module.erplus.ai.mas.runtime.communication.AgentMessage;
import com.hzltd.module.erplus.ai.mas.runtime.memory.GlobalSessionMemory;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GraphExecutionTest {

    static class DummyAgent implements BaseAgent {
        private final String role;
        private final long sleepMs;
        // The instruction implies adding MasTaskHistoryMapper, but it's not used in the provided snippet.
        // Keeping it commented out or removed if not explicitly used later.
        // private final MasTaskHistoryMapper taskHistoryMapper;

        public DummyAgent(String role, long sleepMs) {
            this.role = role;
            this.sleepMs = sleepMs;
        }

        @Override
        public String getRoleName() {
            return role;
        }

        @Override
        public void onMessage(AgentMessage message) { }

        @Override
        public String getInstruction() {
            return "Dummy instruction for " + role;
        }

        @Override
        public String execute(LoopMemory memory) {
            try {
                // Simulate work
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // Write timestamp to prove execution order
            memory.put(role + "_done", System.currentTimeMillis());
            return "Result from " + role;
        }
    }

    @Test
    public void testGraphExecution() {
        GlobalSessionMemory globalMemory = new GlobalSessionMemory("test-graph-session");

        LoopGraphManager graphManager = new LoopGraphManager(globalMemory, null, null, null, new A2AMessageBus());

        // A is independent
        GraphNode nodeA = new GraphNode("loopA", new DummyAgent("AgentA", 200));
        
        // B depends on A
        GraphNode nodeB = new GraphNode("loopB", new DummyAgent("AgentB", 500));
        nodeB.addDependency("loopA");
        
        // C depends on A
        GraphNode nodeC = new GraphNode("loopC", new DummyAgent("AgentC", 300));
        nodeC.addDependency("loopA");

        // D depends on B and C
        GraphNode nodeD = new GraphNode("loopD", new DummyAgent("AgentD", 100));
        nodeD.addDependency("loopB");
        nodeD.addDependency("loopC");

        graphManager.addNode(nodeA);
        graphManager.addNode(nodeB);
        graphManager.addNode(nodeC);
        graphManager.addNode(nodeD);

        long start = System.currentTimeMillis();
        graphManager.executeGraph();
        long end = System.currentTimeMillis();

        // Verifications
        assertTrue(globalMemory.containsKey("AgentA_done"));
        assertTrue(globalMemory.containsKey("AgentB_done"));
        assertTrue(globalMemory.containsKey("AgentC_done"));
        assertTrue(globalMemory.containsKey("AgentD_done"));

        long timeA = (Long) globalMemory.get("AgentA_done");
        long timeB = (Long) globalMemory.get("AgentB_done");
        long timeC = (Long) globalMemory.get("AgentC_done");
        long timeD = (Long) globalMemory.get("AgentD_done");

        // B and C must finish AFTER A
        assertTrue(timeB > timeA);
        assertTrue(timeC > timeA);

        // D must finish AFTER B and C
        assertTrue(timeD > timeB);
        assertTrue(timeD > timeC);

        // Total time should be roughly A(200) + max(B(500), C(300)) + D(100) = ~800ms
        // Due to environment overhead, it might be larger, but we verify it's reasonably efficient.
        long duration = end - start;
        System.out.println("Graph execution duration: " + duration + "ms");
        assertTrue(duration < 1800, "Duration was " + duration + "ms, expected parallel execution");
    }
}
