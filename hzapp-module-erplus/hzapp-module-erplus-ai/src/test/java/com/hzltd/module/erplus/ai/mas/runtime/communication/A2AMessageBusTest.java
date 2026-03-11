package com.hzltd.module.erplus.ai.mas.runtime.communication;

import com.hzltd.module.erplus.ai.mas.runtime.agent.BaseAgent;
import com.hzltd.module.erplus.ai.mas.runtime.memory.LoopMemory;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class A2AMessageBusTest {

    static class MsgDummyAgent implements BaseAgent {
        private final String role;
        public final AtomicReference<AgentMessage> lastReceivedMessage = new AtomicReference<>();
        public CountDownLatch latch;

        public MsgDummyAgent(String role, CountDownLatch latch) {
            this.role = role;
            this.latch = latch;
        }

        @Override
        public String getRoleName() {
            return role;
        }

        @Override
        public void onMessage(AgentMessage message) {
            lastReceivedMessage.set(message);
            if (latch != null) {
                latch.countDown();
            }
        }

        @Override
        public String getInstruction() {
            return "Test task";
        }

        @Override
        public String execute(LoopMemory memory) {
            return "ok";
        }
    }

    @Test
    public void testA2AMessageRouting() throws InterruptedException {
        A2AMessageBus messageBus = new A2AMessageBus();

        CountDownLatch latchA = new CountDownLatch(1);
        CountDownLatch latchB = new CountDownLatch(1);

        MsgDummyAgent agentA = new MsgDummyAgent("AgentA", latchA);
        MsgDummyAgent agentB = new MsgDummyAgent("AgentB", latchB);

        messageBus.register(agentA);
        messageBus.register(agentB);

        // Send Msg to Agent B
        AgentMessage msgToB = AgentMessage.builder()
                .traceId("trace-1")
                .senderRole("AgentA")
                .receiverRole("AgentB")
                .payload("Hello B, please do X")
                .build();

        messageBus.publish(msgToB);

        // Send Msg to Agent A
        AgentMessage msgToA = AgentMessage.builder()
                .traceId("trace-1")
                .senderRole("AgentB")
                .receiverRole("AgentA")
                .payload("Acknowledged X")
                .build();

        messageBus.publish(msgToA);

        // Wait for async delivery
        boolean receivedB = latchB.await(2, TimeUnit.SECONDS);
        boolean receivedA = latchA.await(2, TimeUnit.SECONDS);

        assertTrue(receivedB, "AgentB did not receive message in time");
        assertTrue(receivedA, "AgentA did not receive message in time");

        assertEquals("Hello B, please do X", agentB.lastReceivedMessage.get().getPayload());
        assertEquals("Acknowledged X", agentA.lastReceivedMessage.get().getPayload());
        assertEquals("AgentA", agentB.lastReceivedMessage.get().getSenderRole());
    }
}
