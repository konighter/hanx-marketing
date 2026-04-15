package com.hzltd.module.amz.spapi.service.pipeline;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Visitor interface for parsing Amazon JSON Schema rules (conditions, constraints).
 */
public interface SchemaRuleVisitor {

    /**
     * Called when an if-then logical block is encountered.
     */
    void visitIfThen(JsonNode ifSchema, JsonNode thenSchema, String parentPrefix);

    /**
     * Called for property-level constraints like enum, const, pattern, etc.
     */
    void visitPropertyConstraint(String fieldId, JsonNode constraint);
}
