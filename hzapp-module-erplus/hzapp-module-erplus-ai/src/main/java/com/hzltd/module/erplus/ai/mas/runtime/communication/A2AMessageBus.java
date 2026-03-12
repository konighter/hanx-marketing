package com.hzltd.module.erplus.ai.mas.runtime.communication;

import com.hzltd.module.erplus.ai.mas.runtime.agent.BaseAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Message bus for Agent-to-Agent (A2A) communication within a session.
 * @deprecated Not currently used in the execution pipeline. Will be removed or replaced
 * with a proper inter-agent communication mechanism in a future version.
 */
@Deprecated
@Slf4j
@Service
public class A2AMessageBus {

    // Replay sink to handle race conditions where a response might come before the subscriber is ready.
    private final Sinks.Many<AgentMessage> sink = Sinks.many().replay().limit(50);
    
    // Track active subscribers just for logging/management
    private final Map<String, BaseAgent> subscribers = new ConcurrentHashMap<>();

    public A2AMessageBus() {
        // Start processing the stream continuously
        sink.asFlux().subscribe(
                message -> processMessage(message),
                error -> log.error("[A2AMessageBus] Fatal Error in message stream", error)
        );
    }

    /**
     * Send a message to the bus.
     */
    public void publish(AgentMessage message) {
        log.debug("[A2AMessageBus] Publishing message from [{}] to [{}] in trace [{}]", 
                message.getSenderRole(), message.getReceiverRole(), message.getTraceId());
        
        sink.emitNext(message, Sinks.EmitFailureHandler.busyLooping(java.time.Duration.ofSeconds(1)));
    }

    /**
     * Register an agent to listen for its own messages.
     * (Normally we route from the single sink to the appropriate agent)
     */
    public void register(BaseAgent agent) {
        subscribers.put(agent.getRoleName(), agent);
        log.info("[A2AMessageBus] Registered agent: {}", agent.getRoleName());
    }
    
    public void unregister(BaseAgent agent) {
        subscribers.remove(agent.getRoleName());
    }

    /**
     * Expose raw flux if needed for custom listeners.
     */
    public Flux<AgentMessage> getMessageStream() {
        return sink.asFlux();
    }

    private void processMessage(AgentMessage message) {
        String receiver = message.getReceiverRole();
        BaseAgent agent = subscribers.get(receiver);
        if (agent != null) {
            try {
                // Route to agent inbox asynchronously (prevent blocking the bus thread)
                reactor.core.scheduler.Schedulers.boundedElastic().schedule(() -> {
                    agent.onMessage(message);
                });
            } catch (Exception e) {
                log.error("[A2AMessageBus] Error delivering message to [{}]", receiver, e);
            }
        } else {
            log.warn("[A2AMessageBus] No registered agent for role: {}", receiver);
        }
    }
}
