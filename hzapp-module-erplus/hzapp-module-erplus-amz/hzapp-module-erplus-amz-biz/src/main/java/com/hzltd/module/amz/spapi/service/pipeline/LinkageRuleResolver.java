package com.hzltd.module.amz.spapi.service.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFieldRuleVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormFieldVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.LogicExpressionVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stage 3: Dynamic Linkage Resolution.
 * Parses the 'allOf', 'if', 'then' rules from the Amazon JSON Schema and 
 * converts them into LogicExpressionVO objects that the frontend can execute.
 */
@Slf4j
@Component
public class LinkageRuleResolver {

    public void resolve(JsonNode schemaRoot, List<AmzListingFormFieldVO> fields, PropertyFlattenStrategy.FlattenContext context) {
        JsonNode allOf = schemaRoot.get("allOf");
        if (allOf == null || !allOf.isArray()) {
            return;
        }

        log.info("[LinkageRuleResolver] Resolving {} linkage rules for productType: {}", allOf.size(), context.getProductType());
        
        for (JsonNode rule : allOf) {
            if (rule.has("if") && rule.has("then")) {
                processIfThen(rule.get("if"), rule.get("then"), fields, context);
            }
        }
    }

    private void processIfThen(JsonNode ifNode, JsonNode thenNode, List<AmzListingFormFieldVO> fields, PropertyFlattenStrategy.FlattenContext ctx) {
        // Step 1: Convert 'if' block into a LogicExpression (The trigger)
        // This now resolves bizFields to formFields
        LogicExpressionVO trigger = convertToExpression(ifNode, "", ctx);
        if (trigger == null) return;

        // Step 2: Determine which fields are affected by the 'then' block
        // Resolve target Amazon paths to formFields
        List<AmzListingFormFieldVO> targetFields = extractAffectedFields(thenNode, ctx);
        
        for (AmzListingFormFieldVO targetField : targetFields) {
            markFieldAndChildrenRequired(targetField, trigger);
        }
    }

    private void markFieldAndChildrenRequired(AmzListingFormFieldVO field, LogicExpressionVO trigger) {
        if (field == null) return;
        
        if (field.getLinkages() == null) {
            field.setLinkages(new ArrayList<>());
        }
        
        AmzListingFieldRuleVO ruleVO = AmzListingFieldRuleVO.builder()
                .type("requirement")
                .action("required")
                .conditionLogic(trigger)
                .condition(stringifyExpression(trigger))
                .sourceField(findFirstField(trigger))
                .build();
        field.getLinkages().add(ruleVO);

        // Recursive propagation: if this is a composite field, and children are statically required, 
        // they should also inherit the dynamic requirement from this rule.
        if (field.getChildren() != null) {
            for (AmzListingFormFieldVO child : field.getChildren()) {
                if (Boolean.TRUE.equals(child.getRequired())) {
                    markFieldAndChildrenRequired(child, trigger);
                }
            }
        }
    }

    /**
     * Converts a LogicExpressionVO tree into a readable string for debugging and legacy UI.
     */
    private String stringifyExpression(LogicExpressionVO expr) {
        if (expr == null) return null;
        
        String op = expr.getOperator();
        if ("EQ".equals(op)) {
            return expr.getField() + " == '" + expr.getValue() + "'";
        }
        if ("IN".equals(op)) {
            return expr.getField() + " IN " + expr.getValue();
        }
        
        if (expr.getChildren() == null || expr.getChildren().isEmpty()) return "";
        
        if ("NOT".equals(op)) {
            return "!(" + stringifyExpression(expr.getChildren().get(0)) + ")";
        }
        
        String joiner = "AND".equals(op) ? " && " : " || ";
        return "(" + expr.getChildren().stream()
                .map(this::stringifyExpression)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(joiner)) + ")";
    }

    /**
     * Helper to find a representative source field from a logic tree for legacy support.
     */
    private String findFirstField(LogicExpressionVO expr) {
        if (expr == null) return null;
        if (expr.getField() != null) return expr.getField();
        if (expr.getChildren() != null && !expr.getChildren().isEmpty()) {
            for (LogicExpressionVO child : expr.getChildren()) {
                String f = findFirstField(child);
                if (f != null) return f;
            }
        }
        return null;
    }

    private LogicExpressionVO convertToExpression(JsonNode node, String currentPath, PropertyFlattenStrategy.FlattenContext ctx) {
        if (node.has("allOf")) {
            List<LogicExpressionVO> children = new ArrayList<>();
            node.get("allOf").forEach(n -> children.add(convertToExpression(n, currentPath, ctx)));
            List<LogicExpressionVO> valid = children.stream().filter(java.util.Objects::nonNull).collect(Collectors.toList());
            if (valid.isEmpty()) return null;
            return valid.size() == 1 ? valid.get(0) : LogicExpressionVO.builder().operator("AND").children(valid).build();
        }
        if (node.has("anyOf")) {
            List<LogicExpressionVO> children = new ArrayList<>();
            node.get("anyOf").forEach(n -> children.add(convertToExpression(n, currentPath, ctx)));
            List<LogicExpressionVO> valid = children.stream().filter(java.util.Objects::nonNull).collect(Collectors.toList());
            if (valid.isEmpty()) return null;
            return valid.size() == 1 ? valid.get(0) : LogicExpressionVO.builder().operator("OR").children(valid).build();
        }
        if (node.has("not")) {
            LogicExpressionVO child = convertToExpression(node.get("not"), currentPath, ctx);
            return child != null ? LogicExpressionVO.builder().operator("NOT").children(java.util.Collections.singletonList(child)).build() : null;
        }

        // Recursive property/item/contains traversal
        if (node.has("properties") || node.has("items") || node.has("contains")) {
            List<LogicExpressionVO> children = new ArrayList<>();
            
            if (node.has("properties")) {
                JsonNode props = node.get("properties");
                java.util.Iterator<String> fieldNames = props.fieldNames();
                while (fieldNames.hasNext()) {
                    String name = fieldNames.next();
                    String nextPath = currentPath.isEmpty() ? name : currentPath + "." + name;
                    // Special case for 'attributes' wrapper in core schema
                    if ("attributes".equals(name) && currentPath.isEmpty()) {
                        children.add(convertToExpression(props.get(name), "", ctx));
                    } else {
                        children.add(convertToExpression(props.get(name), nextPath, ctx));
                    }
                }
            }
            
            if (node.has("items")) {
                children.add(convertToExpression(node.get("items"), currentPath, ctx));
            }
            
            if (node.has("contains")) {
                children.add(convertToExpression(node.get("contains"), currentPath, ctx));
            }

            List<LogicExpressionVO> validChildren = children.stream().filter(java.util.Objects::nonNull).collect(Collectors.toList());
            if (validChildren.isEmpty()) return null;
            return validChildren.size() == 1 ? validChildren.get(0) : LogicExpressionVO.builder().operator("AND").children(validChildren).build();
        }

        // Leaf condition components
        if (node.has("const") || node.has("enum")) {
            // Use smart lookup to match schema path to form field
            AmzListingFormFieldVO fieldVO = resolveFieldVO(currentPath, ctx);

            if (fieldVO != null) {
                if (node.has("const")) {
                    return LogicExpressionVO.builder()
                            .operator("EQ")
                            .field(fieldVO.getFormField())
                            .value(node.get("const").asText())
                            .build();
                } else {
                    List<Object> values = new ArrayList<>();
                    node.get("enum").forEach(v -> values.add(v.asText()));
                    return LogicExpressionVO.builder()
                            .operator("IN")
                            .field(fieldVO.getFormField())
                            .value(values)
                            .build();
                }
            }
        }

        return null;
    }

    private List<AmzListingFormFieldVO> extractAffectedFields(JsonNode thenNode, PropertyFlattenStrategy.FlattenContext ctx) {
        List<AmzListingFormFieldVO> affected = new ArrayList<>();
        if (thenNode.has("required")) {
            for (JsonNode idNode : thenNode.get("required")) {
                String amazonId = idNode.asText();
                // Match by bizField using smart lookup
                AmzListingFormFieldVO field = resolveFieldVO(amazonId, ctx);
                if (field != null) affected.add(field);
            }
        }
        
        // Handle nested properties in 'then' (e.g., hidden or other constraints)
        if (thenNode.has("properties")) {
            JsonNode props = thenNode.get("properties");
            java.util.Iterator<String> it = props.fieldNames();
            while (it.hasNext()) {
                String name = it.next();
                AmzListingFormFieldVO field = resolveFieldVO(name, ctx);
                if (field != null) affected.add(field);
            }
        }
        return affected;
    }

    /**
     * Smart lookup that tries exact, suffix indexed, and normalized matches
     */
    private AmzListingFormFieldVO resolveFieldVO(String path, PropertyFlattenStrategy.FlattenContext ctx) {
        if (path == null) return null;
        Map<String, AmzListingFormFieldVO> map = ctx.getBizPathToFieldMap();
        
        // 1. Exact match
        if (map.containsKey(path)) return map.get(path);
        
        // 2. Trailing index match (common for scalar wrappers)
        if (map.containsKey(path + ".0")) return map.get(path + ".0");
        if (map.containsKey(path + ".0.value")) return map.get(path + ".0.value");
        
        // 3. Multi-level index match (normalized)
        String normalized = path.replace(".0.", ".").replaceAll("\\.0$", "");
        if (map.containsKey(normalized)) return map.get(normalized);
        
        return null;
    }
}
