package com.hzltd.module.erplus.ai.mas.communication;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

/**
 * Message payload for Agent-to-Agent (A2A) communication.
 */
@Data
@Builder
public class AgentMessage {
    /**
     * The ID of the loop or session this message belongs to.
     */
    private String traceId;

    /**
     * The sender agent role.
     */
    private String senderRole;

    /**
     * The target receiver agent role.
     */
    private String receiverRole;

    /**
     * The type of message (PROPOSAL, REVIEW, ACCEPT, etc.)
     */
    private AgentMessageType type;

    /**
     * The actual message content or instruction payload.
     */
    private String payload;

    /**
     * Optional structured metadata.
     */
    private Map<String, Object> metadata;
}
