package com.hzltd.module.amz.spapi.service.pipeline;

import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.module.amz.spapi.controller.admin.listing.vo.AmzListingFieldRuleVO;
import com.hzltd.module.amz.spapi.controller.admin.listing.vo.AmzListingFormFieldVO;
import com.hzltd.module.amz.spapi.controller.admin.listing.vo.LogicExpressionVO;
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
        log.info("[LinkageRuleResolver] Starting recursive linkage resolution for productType: {}", context.getProductType());
        processRecursive(schemaRoot, null, fields, context);
    }

    private void processRecursive(JsonNode node, LogicExpressionVO parentCondition, List<AmzListingFormFieldVO> fields, PropertyFlattenStrategy.FlattenContext context) {
        if (node == null || node.isMissingNode()) return;

        // 1. Handle allOf / anyOf (Standard containers for rules)
        if (node.has("allOf") && node.get("allOf").isArray()) {
            for (JsonNode subNode : node.get("allOf")) {
                processRecursive(subNode, parentCondition, fields, context);
            }
        }
        if (node.has("anyOf") && node.get("anyOf").isArray()) {
            for (JsonNode subNode : node.get("anyOf")) {
                processRecursive(subNode, parentCondition, fields, context);
            }
        }

        // 2. Handle if-then-else
        if (node.has("if") && node.has("then")) {
            LogicExpressionVO currentIf = convertToExpression(node.get("if"), "", context);
            if (currentIf != null) {
                // Combine with parent condition if exists
                LogicExpressionVO thenCondition = combineConditions(parentCondition, currentIf, "AND");
                
                // Recurse into 'then' block
                processRecursive(node.get("then"), thenCondition, fields, context);

                // Recurse into 'else' block if exists with negated condition
                if (node.has("else")) {
                    LogicExpressionVO negatedIf = LogicExpressionVO.builder()
                            .operator("NOT")
                            .children(java.util.Collections.singletonList(currentIf))
                            .build();
                    LogicExpressionVO elseCondition = combineConditions(parentCondition, negatedIf, "AND");
                    processRecursive(node.get("else"), elseCondition, fields, context);
                }
            }
        }

        // 3. Handle required fields at this level (either from a 'then' or a standalone block)
        if (node.has("required") && node.get("required").isArray() && parentCondition != null) {
            for (JsonNode idNode : node.get("required")) {
                AmzListingFormFieldVO targetField = resolveFieldVO(idNode.asText(), context);
                if (targetField != null) {
                    markFieldAndChildrenRequired(targetField, parentCondition);
                }
            }
        }

        // 4. Handle nested properties in 'then' / 'else'
        if (node.has("properties") && parentCondition != null) {
            // This captures visiblity or other metadata-based rules inside the block
            List<AmzListingFormFieldVO> affectedFields = extractAffectedFields(node, context);
            for (AmzListingFormFieldVO targetField : affectedFields) {
                // Currently visiblity rules are handled by extractAffectedFields and additional downstream processing,
                // But for requirements triggered by nested properties, we apply the condition here.
                // (Note: Amazon SP-API schema usually puts 'required' at the object level, not inside properties)
            }
        }
    }

    private LogicExpressionVO combineConditions(LogicExpressionVO c1, LogicExpressionVO c2, String operator) {
        if (c1 == null) return c2;
        if (c2 == null) return c1;
        return simplify(LogicExpressionVO.builder()
                .operator(operator)
                .children(java.util.Arrays.asList(c1, c2))
                .build());
    }

    private void markFieldAndChildrenRequired(AmzListingFormFieldVO field, LogicExpressionVO trigger) {
        if (field == null || trigger == null) return;
        
        if (field.getLinkages() == null) {
            field.setLinkages(new ArrayList<>());
        }
        
        // Universal Solution: Rule Consolidation
        // Check if there is already a 'required' rule for this field.
        // If so, merge them with AND to ensure that constraints from all branches are met.
        AmzListingFieldRuleVO existingRule = field.getLinkages().stream()
                .filter(r -> "requirement".equals(r.getType()) && "required".equals(r.getAction()))
                .findFirst()
                .orElse(null);

        if (existingRule != null) {
            LogicExpressionVO merged = combineConditions(existingRule.getConditionLogic(), trigger, "AND");
            existingRule.setConditionLogic(merged);
            existingRule.setCondition(stringifyExpression(merged));
            existingRule.setSourceField(findFirstField(merged));
        } else {
            AmzListingFieldRuleVO ruleVO = AmzListingFieldRuleVO.builder()
                    .type("requirement")
                    .action("required")
                    .conditionLogic(trigger)
                    .condition(stringifyExpression(trigger))
                    .sourceField(findFirstField(trigger))
                    .build();
            field.getLinkages().add(ruleVO);
        }

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
        
        if (expr.getChildren() == null || expr.getChildren().isEmpty()) {
            if ("EMPTY".equals(op)) {
                return expr.getField() + " IS EMPTY";
            }
            return "";
        }
        
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
        if (node == null || node.isMissingNode()) return null;

        List<LogicExpressionVO> subExpressions = new ArrayList<>();

        // 1. Structural Keywords (allOf, anyOf, not)
        if (node.has("allOf") && node.get("allOf").isArray()) {
            List<LogicExpressionVO> children = new ArrayList<>();
            node.get("allOf").forEach(n -> {
                LogicExpressionVO child = convertToExpression(n, currentPath, ctx);
                if (child != null) children.add(child);
            });
            if (!children.isEmpty()) {
                subExpressions.add(children.size() == 1 ? children.get(0) : LogicExpressionVO.builder().operator("AND").children(children).build());
            }
        }
        if (node.has("anyOf") && node.get("anyOf").isArray()) {
            List<LogicExpressionVO> children = new ArrayList<>();
            node.get("anyOf").forEach(n -> {
                LogicExpressionVO child = convertToExpression(n, currentPath, ctx);
                if (child != null) children.add(child);
            });
            if (!children.isEmpty()) {
                subExpressions.add(children.size() == 1 ? children.get(0) : LogicExpressionVO.builder().operator("OR").children(children).build());
            }
        }
        if (node.has("not")) {
            LogicExpressionVO child = convertToExpression(node.get("not"), currentPath, ctx);
            if (child != null) {
                subExpressions.add(LogicExpressionVO.builder().operator("NOT").children(java.util.Collections.singletonList(child)).build());
            }
        }

        // 2. Existence Keywords (required)
        if (node.has("required") && node.get("required").isArray()) {
            for (JsonNode reqNode : node.get("required")) {
                String fieldName = reqNode.asText();
                String fullPath = currentPath.isEmpty() ? fieldName : currentPath + "." + fieldName;
                AmzListingFormFieldVO fieldVO = resolveFieldVO(fullPath, ctx);
                if (fieldVO != null) {
                    // "required" in an if-block means "NOT EMPTY"
                    subExpressions.add(LogicExpressionVO.builder()
                            .operator("NOT")
                            .children(java.util.Collections.singletonList(
                                    LogicExpressionVO.builder().operator("EMPTY").field(fieldVO.getFormField()).build()
                            ))
                            .build());
                }
            }
        }

        // 3. Property Keywords (properties, items, contains)
        if (node.has("properties") || node.has("items") || node.has("contains")) {
            if (node.has("properties")) {
                JsonNode props = node.get("properties");
                java.util.Iterator<String> fieldNames = props.fieldNames();
                while (fieldNames.hasNext()) {
                    String name = fieldNames.next();
                    // Special case for 'attributes' wrapper in core schema
                    if ("attributes".equals(name) && currentPath.isEmpty()) {
                        LogicExpressionVO attrExpr = convertToExpression(props.get(name), "", ctx);
                        if (attrExpr != null) subExpressions.add(attrExpr);
                    } else {
                        String nextPath = currentPath.isEmpty() ? name : currentPath + "." + name;
                        LogicExpressionVO propExpr = convertToExpression(props.get(name), nextPath, ctx);
                        if (propExpr != null) subExpressions.add(propExpr);
                    }
                }
            }
            if (node.has("items")) {
                LogicExpressionVO itemExpr = convertToExpression(node.get("items"), currentPath, ctx);
                if (itemExpr != null) subExpressions.add(itemExpr);
            }
            if (node.has("contains")) {
                LogicExpressionVO contExpr = convertToExpression(node.get("contains"), currentPath, ctx);
                if (contExpr != null) subExpressions.add(contExpr);
            }
        }

        // 4. Value Keywords (const, enum)
        if (node.has("const") || node.has("enum")) {
            AmzListingFormFieldVO fieldVO = resolveFieldVO(currentPath, ctx);
            if (fieldVO != null) {
                if (node.has("const")) {
                    subExpressions.add(LogicExpressionVO.builder()
                            .operator("EQ")
                            .field(fieldVO.getFormField())
                            .value(node.get("const").asText())
                            .build());
                } else {
                    List<Object> values = new ArrayList<>();
                    node.get("enum").forEach(v -> values.add(v.asText()));
                    subExpressions.add(LogicExpressionVO.builder()
                            .operator("IN")
                            .field(fieldVO.getFormField())
                            .value(values)
                            .build());
                }
            }
        }

        List<LogicExpressionVO> valid = subExpressions.stream().filter(java.util.Objects::nonNull).collect(Collectors.toList());
        if (valid.isEmpty()) return null;
        return simplify(valid.size() == 1 ? valid.get(0) : LogicExpressionVO.builder().operator("AND").children(valid).build());
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

    private LogicExpressionVO simplify(LogicExpressionVO expr) {
        if (expr == null) return null;
        
        // 1. Flatten nested logic of same type: AND(A, AND(B,C)) -> AND(A, B, C)
        if (expr.getChildren() != null && !expr.getChildren().isEmpty()) {
            List<LogicExpressionVO> flatChildren = new ArrayList<>();
            for (LogicExpressionVO child : expr.getChildren()) {
                LogicExpressionVO sChild = simplify(child);
                if (expr.getOperator().equals(sChild.getOperator()) && ("AND".equals(expr.getOperator()) || "OR".equals(expr.getOperator()))) {
                    flatChildren.addAll(sChild.getChildren());
                } else {
                    flatChildren.add(sChild);
                }
            }
            
            // Dedup children by stringified form
            List<LogicExpressionVO> uniques = new ArrayList<>();
            java.util.Set<String> seen = new java.util.HashSet<>();
            for (LogicExpressionVO child : flatChildren) {
                if (seen.add(stringifyExpression(child))) {
                    uniques.add(child);
                }
            }
            
            if (uniques.size() == 1 && ("AND".equals(expr.getOperator()) || "OR".equals(expr.getOperator()))) {
                return uniques.get(0);
            }
            expr.setChildren(uniques);
        }

        // 2. Double Negation: NOT(NOT(X)) -> X
        if ("NOT".equals(expr.getOperator()) && expr.getChildren() != null && expr.getChildren().size() == 1) {
            LogicExpressionVO child = expr.getChildren().get(0);
            if ("NOT".equals(child.getOperator()) && child.getChildren() != null && child.getChildren().size() == 1) {
                return simplify(child.getChildren().get(0));
            }
        }

        return expr;
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
