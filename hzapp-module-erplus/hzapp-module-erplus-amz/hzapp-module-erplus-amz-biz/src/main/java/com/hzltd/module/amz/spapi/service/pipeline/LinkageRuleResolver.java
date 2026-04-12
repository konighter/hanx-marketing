package com.hzltd.module.amz.spapi.service.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFieldRuleVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormFieldVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.LogicExpressionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stage 3: Dynamic Linkage Resolution.
 * Parses the 'allOf', 'if', 'then' rules from the Amazon JSON Schema and 
 * converts them into LogicExpressionVO objects that the frontend can execute.
 */
@Slf4j
@Component
public class LinkageRuleResolver {

    public void resolve(JsonNode schemaRoot, List<AmzListingFormFieldVO> fields) {
        JsonNode allOf = schemaRoot.get("allOf");
        if (allOf == null || !allOf.isArray()) {
            return;
        }

        log.info("[LinkageRuleResolver] Resolving {} linkage rules", allOf.size());
        
        for (JsonNode rule : allOf) {
            if (rule.has("if") && rule.has("then")) {
                processIfThen(rule.get("if"), rule.get("then"), fields);
            }
        }
    }

    private void processIfThen(JsonNode ifNode, JsonNode thenNode, List<AmzListingFormFieldVO> fields) {
        // Step 1: Convert 'if' block into a LogicExpression (The trigger)
        LogicExpressionVO trigger = convertToExpression(ifNode);
        if (trigger == null) return;

        // Step 2: Determine which fields are affected by the 'then' block
        // 'thenNode' usually contains 'required' or nested 'properties'
        List<String> affectedFieldIds = extractAffectedFieldIds(thenNode);
        
        for (String fieldId : affectedFieldIds) {
            // Find the field in the flat list and attach the linkage rule
            AmzListingFormFieldVO targetField = findField(fields, fieldId);
            if (targetField != null) {
                if (targetField.getLinkages() == null) {
                    targetField.setLinkages(new ArrayList<>());
                }
                // The linkage rule tells the frontend: "This field is REQUIRED if TRIGGER is met"
                AmzListingFieldRuleVO ruleVO = AmzListingFieldRuleVO.builder()
                        .type("requirement")
                        .action("required")
                        .conditionLogic(trigger)
                        .build();
                targetField.getLinkages().add(ruleVO);
            }
        }
    }

    private LogicExpressionVO convertToExpression(JsonNode node) {
        // Handle nested properties in 'if'
        if (node.has("properties")) {
            JsonNode props = node.get("properties");
            String fieldName = props.fieldNames().next();
            JsonNode constraint = props.get(fieldName);
            
            if (constraint.has("const")) {
                return LogicExpressionVO.builder()
                        .operator("EQ")
                        .field(fieldName)
                        .value(constraint.get("const").asText())
                        .build();
            } else if (constraint.has("enum")) {
                List<Object> values = new ArrayList<>();
                constraint.get("enum").forEach(v -> values.add(v.asText()));
                return LogicExpressionVO.builder()
                        .operator("IN")
                        .field(fieldName)
                        .value(values)
                        .build();
            }
        }
        return null;
    }

    private List<String> extractAffectedFieldIds(JsonNode thenNode) {
        List<String> ids = new ArrayList<>();
        if (thenNode.has("required")) {
            thenNode.get("required").forEach(id -> ids.add(id.asText()));
        }
        // Recursive extraction for nested properties in 'then' can be added here
        return ids;
    }

    private AmzListingFormFieldVO findField(List<AmzListingFormFieldVO> fields, String id) {
        for (AmzListingFormFieldVO f : fields) {
            if (id.equals(f.getId())) return f;
            if (f.getChildren() != null) {
                AmzListingFormFieldVO found = findField(f.getChildren(), id);
                if (found != null) return found;
            }
        }
        return null;
    }
}
