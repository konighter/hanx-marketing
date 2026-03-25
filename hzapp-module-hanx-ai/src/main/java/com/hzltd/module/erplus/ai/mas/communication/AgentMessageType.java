package com.hzltd.module.erplus.ai.mas.communication;

import lombok.Getter;

/**
 * Types of messages exchanged between agents to support structured collaboration protocols.
 */
@Getter
public enum AgentMessageType {
    /**
     * A formal proposal of a plan or result for peer review.
     */
    PROPOSAL("PROPOSAL"),

    /**
     * A review feedback on a proposal.
     */
    REVIEW("REVIEW"),

    /**
     * Acceptance of a proposal.
     */
    ACCEPT("ACCEPT"),

    /**
     * Rejection of a proposal, usually with feedback.
     */
    REJECT("REJECT"),

    /**
     * General information or status update.
     */
    INFO("INFO"),

    /**
     * A direct instruction or task assignment.
     */
    COMMAND("COMMAND");

    private final String code;

    AgentMessageType(String code) {
        this.code = code;
    }
}
