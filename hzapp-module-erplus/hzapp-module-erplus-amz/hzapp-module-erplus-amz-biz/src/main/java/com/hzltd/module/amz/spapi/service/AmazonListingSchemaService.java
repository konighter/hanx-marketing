package com.hzltd.module.amz.spapi.service;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFieldRuleVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormConfigVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.AmzListingFormFieldVO;
import com.hzltd.module.amz.spapi.controller.admin.vo.LogicExpressionVO;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.hzltd.module.erplus.system.model.CrossMetaCategoryModel;
import com.hzltd.module.erplus.system.service.SystemMetaCategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for parsing Amazon SP-API JSON Schemas and generating UI-ready configurations.
 */
@Slf4j
@Service
public class AmazonListingSchemaService {

    private static final Set<String> IMPORTANT_FIELDS_WHITELIST = Set.of(
            "bullet_point", "product_description", "purchasable_offer", "fulfillment_availability",
            "item_type_keyword", "condition_type", "externally_assigned_product_identifier",
            "list_price"
    );

    @Resource
    private SystemMetaCategoryService metaCategoryService;

    /**
     * Generates a VO configuration for a specific product type.
     */
    public AmzListingFormConfigVO generateFormConfig(String productType) {

        CrossMetaCategoryModel crossMetaCategory = metaCategoryService.getCrossMetaCategoryByPlatformCategoryCode(CrossPlatformEnum.AMAZON, productType);

        if (crossMetaCategory == null || StrUtil.isEmpty(crossMetaCategory.getExtra())) {
            log.warn("[generateFormConfig] No schema found for productType: {}", productType);
            return null;
        }

        JsonNode schemaRoot = JsonUtils.parseTree(crossMetaCategory.getExtra());
        AmzListingFormConfigVO config = new AmzListingFormConfigVO();
        config.setProductType(productType);
        
        List<AmzListingFormFieldVO> fields = new ArrayList<>();
        Map<String, String> fieldMapping = new HashMap<>();
        
        // 1. Recursive Parse Properties
        flattenProperties("", schemaRoot, fields, fieldMapping, true);

        // 2. Parse Linkage Rules (allOf)
        parseLinkageRules(schemaRoot, fields);

        config.setFields(fields);
        config.setFieldMapping(fieldMapping);
        return config;
    }

    private enum FieldStructureType {
        LEAF, PSEUDO_ARRAY, TRUE_ARRAY, NESTED
    }

    private void flattenProperties(String prefix, JsonNode node, List<AmzListingFormFieldVO> fields, Map<String, String> mapping, boolean parentIsRequired) {
        JsonNode properties = node.get("properties");
        if (properties == null || !properties.isObject()) return;

        Set<String> requiredAtLevel = extractRequiredAtLevel(node);
        
        Iterator<Map.Entry<String, JsonNode>> it = properties.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            String propName = entry.getKey();
            JsonNode propSchema = entry.getValue();
            String fullId = StrUtil.isEmpty(prefix) ? propName : prefix + "." + propName;

            if (isInternalMetaField(propName) || isERPMasterField(propName)) continue;

            boolean isRequired = parentIsRequired && requiredAtLevel.contains(propName);
            FieldStructureType structureType = determineFieldStructure(propSchema);

            switch (structureType) {
                case PSEUDO_ARRAY -> {
                    // Record mapping for the original field name
                    if (StrUtil.isEmpty(prefix)) {
                        JsonNode items = propSchema.get("items");
                        if (isAmazonValueWrapper(items)) {
                            mapping.put(propName, fullId + ".0.value");
                        } else {
                            mapping.put(propName, fullId + ".0");
                        }
                    }
                    // Flatten to .0 index recursively
                    flattenProperties(fullId + ".0", propSchema.get("items"), fields, mapping, isRequired);
                }
                case TRUE_ARRAY -> {
                    if (StrUtil.isEmpty(prefix)) mapping.put(propName, fullId);
                    fields.add(parseField(fullId, propSchema, isRequired));
                }
                case NESTED -> {
                    flattenProperties(fullId, propSchema, fields, mapping, isRequired);
                }
                case LEAF -> {
                    if (StrUtil.isEmpty(prefix)) mapping.put(propName, fullId);
                    fields.add(parseField(fullId, propSchema, isRequired));
                }
            }
        }
    }

    private FieldStructureType determineFieldStructure(JsonNode schema) {
        String type = schema.path("type").asText("string");
        
        if ("array".equals(type) && schema.has("items")) {
            JsonNode items = schema.get("items");
            int maxItems = schema.path("maxUniqueItems").asInt(schema.path("maxItems").asInt(Integer.MAX_VALUE));
            
            // Aggressive Pseudo-Array classification: any array with max 1 item and properties is flattened
            if (maxItems == 1 && items.has("properties")) {
                return FieldStructureType.PSEUDO_ARRAY;
            }
            return FieldStructureType.TRUE_ARRAY;
        }
        
        if ("object".equals(type) && schema.has("properties")) {
            return FieldStructureType.NESTED;
        }
        
        return FieldStructureType.LEAF;
    }

    private boolean isAmazonValueWrapper(JsonNode items) {
        JsonNode properties = items.get("properties");
        if (properties == null || !properties.isObject()) return false;
        
        // Amazon wrappers typically only contain value, marketplace_id, language_tag, and sometimes type (for external IDs)
        Iterator<String> fieldNames = properties.fieldNames();
        while (fieldNames.hasNext()) {
            String name = fieldNames.next();
            if (!name.equals("value") && !name.equals("marketplace_id") && !name.equals("language_tag") && !name.equals("type")) {
                return false;
            }
        }
        return properties.has("value");
    }

    private boolean isInternalMetaField(String name) {
        return name.equals("marketplace_id") || name.equals("language_tag");
    }

    private boolean isERPMasterField(String name) {
        return name.equals("item_name") ||
                name.equals("brand") ||
//                name.equals("manufacturer") ||
//                name.equals("part_number") ||
//                name.equals("model_number") ||
//                name.equals("model_name") ||
                name.contains("product_image_locator"); // Catches main, other_X, swatch_..., pt360_...
    }

    private Set<String> extractRequiredAtLevel(JsonNode node) {
        Set<String> required = new HashSet<>();
        JsonNode reqNode = node.get("required");
        if (reqNode != null && reqNode.isArray()) {
            for (JsonNode item : reqNode) {
                required.add(item.asText());
            }
        }
        return required;
    }

    private AmzListingFormFieldVO parseField(String id, JsonNode schema, boolean isRequired) {
        boolean hidden = schema.path("hidden").asBoolean(false);
        boolean isOptional = !isRequired && !isImportantField(id);

        AmzListingFormFieldVO.AmzListingFormFieldVOBuilder builder = AmzListingFormFieldVO.builder()
                .id(id)
                .title(cleanLabel(schema.path("title").asText(id)))
                .description(schema.path("description").asText(""))
                .required(isRequired)
                .editable(schema.path("editable").asBoolean(true))
                .hidden(hidden)
                .optional(isOptional)
                .linkageRules(new ArrayList<>());

        // Assign specialized UI widgets
        if (id.contains("bullet_point")) {
            builder.uiWidget("bullet_point_editor");
        }

        // Extract Constraints for extra data
        Map<String, Object> extra = new HashMap<>();
        if (schema.has("minimum")) extra.put("minimum", schema.get("minimum").asDouble());
        if (schema.has("maximum")) extra.put("maximum", schema.get("maximum").asDouble());
        builder.extra(extra);

        // Type Detection & Option Extraction
        extractTypeAndOptions(builder, schema);

        // Set default value if present
        if (schema.has("default")) {
            builder.defaultValue(JsonUtils.parseObject(schema.get("default").toString(), Object.class));
        }

        return builder.build();
    }

    private void extractTypeAndOptions(AmzListingFormFieldVO.AmzListingFormFieldVOBuilder builder, JsonNode node) {
        // 1. Try to extract options directly
        if (tryExtractOptions(builder, node)) {
            return;
        }

        // 2. If it's an array, try to extract options from its items or nested value wrapper
        if (node.path("type").asText().equals("array") && node.has("items")) {
            JsonNode items = node.get("items");
            if (tryExtractOptions(builder, items)) {
                return;
            }
            // Deep look for Amazon wrapper pattern in items: { properties: { value: { enum: ... } } }
            if (items.has("properties") && items.get("properties").has("value")) {
                if (tryExtractOptions(builder, items.get("properties").get("value"))) {
                    return;
                }
            }
        }

        // 3. Fallback to basic type detection
        String type = node.path("type").asText("string");
        String format = node.path("format").asText("");
        
        if ("date".equals(format) || "date-time".equals(format)) {
            builder.type("date");
        } else if (node.has("oneOf")) {
            boolean isDate = false;
            for (JsonNode sub : node.get("oneOf")) {
                if ("date".equals(sub.path("format").asText("")) || 
                    "date-time".equals(sub.path("format").asText(""))) {
                    isDate = true;
                    break;
                }
            }
            builder.type(isDate ? "date" : type);
        } else {
            builder.type(type);
        }
    }

    private boolean tryExtractOptions(AmzListingFormFieldVO.AmzListingFormFieldVOBuilder builder, JsonNode node) {
        if (node.has("enum")) {
            builder.type("enum");
            builder.options(extractOptions(node));
            return true;
        } else if (node.has("anyOf")) {
            List<AmzListingFormFieldVO.Option> options = new ArrayList<>();
            for (JsonNode sub : node.get("anyOf")) {
                if (sub.has("enum")) {
                    options.addAll(extractOptions(sub));
                }
            }
            if (!options.isEmpty()) {
                builder.type("enum");
                builder.options(options);
                return true;
            }
        }
        return false;
    }

    private String cleanLabel(String label) {
        if (StrUtil.isEmpty(label)) return label;
        // Optimization: Remove trailing English translations and keeping the Chinese part.
        // Example: "产品描述 Product Description" -> "产品描述"
        // Example: "电池 Batteries (Required)" -> "电池"
        if (label.matches(".*[\\u4e00-\\u9fa5].*")) {
             // Look for Chinese character followed by space/separator then English
             // This avoids deleting English-only acronyms like "CBM" or "EU" if they are the only content.
             // We use a more relaxed regex for the suffix to catch parentheses and other signs.
             return label.replaceAll("([\\u4e00-\\u9fa5]+)[\\s/\\\\-]+[A-Za-z0-9\\s\\(\\)\\[\\]/\\\\-]+$", "$1").trim();
        }
        return label;
    }

    private List<AmzListingFormFieldVO.Option> extractOptions(JsonNode node) {
        List<AmzListingFormFieldVO.Option> options = new ArrayList<>();
        JsonNode enums = node.get("enum");
        JsonNode names = node.get("enumNames");
        
        for (int i = 0; i < enums.size(); i++) {
            String val = enums.get(i).asText();
            String label = (names != null && names.size() > i) ? names.get(i).asText() : val;
            options.add(new AmzListingFormFieldVO.Option(cleanLabel(label), val));
        }
        return options;
    }

    private void parseLinkageRules(JsonNode root, List<AmzListingFormFieldVO> fields) {
        JsonNode allOf = root.get("allOf");
        if (allOf == null || !allOf.isArray()) return;

        for (JsonNode entry : allOf) {
            if (entry.has("if") && entry.has("then")) {
                parseConditionAndAction(entry.get("if"), entry.get("then"), fields);
            }
        }
    }

    private void parseConditionAndAction(JsonNode ifNode, JsonNode thenNode, List<AmzListingFormFieldVO> fields) {
        LogicExpressionVO condition = parseLogic(ifNode, "");
        if (condition == null) return;

        // Process 'then' effects
        // 1. Required fields
        JsonNode reqNode = thenNode.get("required");
        if (reqNode != null && reqNode.isArray()) {
            for (JsonNode targetFieldNode : reqNode) {
                String targetFieldId = targetFieldNode.asText();
                // If it's a pseudo-array at the top level, add rules to the .0.value subfields
                addRulesToField(fields, targetFieldId, condition, "requirement", "required");
            }
        }

        // 2. Visibility / Editable
        JsonNode thenProps = thenNode.path("properties");
        if (thenProps.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = thenProps.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String targetFieldId = entry.getKey();
                JsonNode meta = entry.getValue();
                
                if (meta.has("hidden")) {
                    String action = meta.get("hidden").asBoolean() ? "hide" : "show";
                    addRulesToField(fields, targetFieldId, condition, "visibility", action);
                }
                
                if (meta.has("required")) {
                    for (JsonNode subReq : meta.get("required")) {
                        addRulesToField(fields, targetFieldId + "." + subReq.asText(), condition, "requirement", "required");
                    }
                }
            }
        }
    }

    private LogicExpressionVO parseLogic(JsonNode node, String prefix) {
        if (node == null || node.isMissingNode()) return null;

        List<LogicExpressionVO> subExpressions = new ArrayList<>();

        // 1. Logical Connectives
        if (node.has("allOf")) subExpressions.addAll(parseLogicList(node.get("allOf"), prefix));
        if (node.has("anyOf")) {
            List<LogicExpressionVO> children = parseLogicList(node.get("anyOf"), prefix);
            if (!children.isEmpty()) subExpressions.add(new LogicExpressionVO("OR", null, null, children));
        }
        if (node.has("not")) {
            LogicExpressionVO child = parseLogic(node.get("not"), prefix);
            if (child != null) subExpressions.add(new LogicExpressionVO("NOT", null, null, Collections.singletonList(child)));
        }

        // 2. Property-based constraints
        if (node.has("properties")) {
            Iterator<Map.Entry<String, JsonNode>> it = node.get("properties").fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String fieldName = entry.getKey();
                JsonNode constraint = entry.getValue();
                String fullId = StrUtil.isEmpty(prefix) ? fieldName : prefix + "." + fieldName;

                // Determine structure to adjust path
                FieldStructureType type = determineFieldStructure(constraint);
                if (type == FieldStructureType.PSEUDO_ARRAY) {
                    // Force logic to target the flattened .0.value
                    subExpressions.add(parseFieldConstraint(fullId + ".0.value", constraint.get("items").get("properties").get("value")));
                } else {
                    subExpressions.add(parseFieldConstraint(fullId, constraint));
                }
            }
        }

        // 3. Required constraints
        if (node.has("required")) {
            for (JsonNode req : node.get("required")) {
                String fieldName = req.asText();
                String fullId = StrUtil.isEmpty(prefix) ? fieldName : prefix + "." + fieldName;
                
                // If the required field is a pseudo-array, we require its value leaf
                JsonNode propSchema = node.path("properties").get(fieldName);
                if (propSchema != null && determineFieldStructure(propSchema) == FieldStructureType.PSEUDO_ARRAY) {
                    subExpressions.add(new LogicExpressionVO("REQUIRED", fullId + ".0.value", null, null));
                } else {
                    subExpressions.add(new LogicExpressionVO("REQUIRED", fullId, null, null));
                }
            }
        }

        // 4. Direct value leaf constraints
        if (node.has("const")) {
            subExpressions.add(new LogicExpressionVO("EQ", prefix, node.get("const").asText(), null));
        } else if (node.has("enum")) {
            subExpressions.add(new LogicExpressionVO("EQ", prefix, node.get("enum").get(0).asText(), null));
        }

        if (subExpressions.isEmpty()) return null;
        LogicExpressionVO root = (subExpressions.size() == 1) ? subExpressions.get(0) : new LogicExpressionVO("AND", null, null, subExpressions);
        return simplify(root);
    }

    private LogicExpressionVO simplify(LogicExpressionVO expr) {
        if (expr == null) return null;

        // 1. Recurse into children
        if (expr.getChildren() != null) {
            List<LogicExpressionVO> simplifiedChildren = new ArrayList<>();
            for (LogicExpressionVO child : expr.getChildren()) {
                LogicExpressionVO simplifiedChild = simplify(child);
                if (simplifiedChild != null) {
                    // Flatten nested logic of same type: AND(A, AND(B,C)) -> AND(A, B, C)
                    if (expr.getOperator().equals(simplifiedChild.getOperator()) && 
                        ("AND".equals(expr.getOperator()) || "OR".equals(expr.getOperator()))) {
                        simplifiedChildren.addAll(simplifiedChild.getChildren());
                    } else {
                        simplifiedChildren.add(simplifiedChild);
                    }
                }
            }
            expr.setChildren(simplifiedChildren);
        }

        // 2. Recurse into value (Deep simplification for nested logic)
        if (expr.getValue() instanceof LogicExpressionVO) {
            expr.setValue(simplify((LogicExpressionVO) expr.getValue()));
        }

        // 3. Absorption & Negation Rules
        if ("NOT".equals(expr.getOperator()) && expr.getChildren().size() == 1) {
            LogicExpressionVO child = expr.getChildren().get(0);
            if ("NOT".equals(child.getOperator())) {
                return simplify(child.getChildren().get(0)); // !(!A) -> A
            }
        }

        // 4. Eliminate redundancy in AND blocks
        if ("AND".equals(expr.getOperator()) && expr.getChildren() != null) {
            // A. Remove duplicates first
            List<LogicExpressionVO> uniques = new ArrayList<>();
            Set<String> seen = new HashSet<>();
            for (LogicExpressionVO child : expr.getChildren()) {
                if (seen.add(child.toString())) uniques.add(child);
            }
            expr.setChildren(uniques);

            // B. Inclusion: Remove REQUIRED(X) if EQ(X, ...) is present
            Set<String> fieldsWithValues = new HashSet<>();
            for (LogicExpressionVO child : expr.getChildren()) {
                if ("EQ".equals(child.getOperator())) fieldsWithValues.add(child.getField());
            }
            expr.getChildren().removeIf(child -> "REQUIRED".equals(child.getOperator()) && fieldsWithValues.contains(child.getField()));

            // C. Parent-Child Pruning: Remove REQUIRED(P) if P.child is present
            Set<String> allConcreteFields = expr.getChildren().stream()
                    .filter(c -> c.getField() != null)
                    .map(LogicExpressionVO::getField)
                    .collect(Collectors.toSet());
        expr.getChildren().removeIf(child -> {
                if (!"REQUIRED".equals(child.getOperator()) || child.getField() == null) return false;
                String f = child.getField();
                return allConcreteFields.stream().anyMatch(other -> other.startsWith(f + ".") && !other.equals(f));
            });
        }

        // 5. Advanced Factoring in OR blocks: (A && C) || (B && C) -> (A || B) && C
        if ("OR".equals(expr.getOperator()) && expr.getChildren() != null && expr.getChildren().size() > 1) {
            // A. Presence-Equality Consolidation: (X == null || X.0.value != 'v') -> X.0.value != 'v'
            // We use a safe comparison that allows one field to be a prefix of another
            if (expr.getChildren().size() == 2) {
                 LogicExpressionVO c1 = expr.getChildren().get(0);
                 LogicExpressionVO c2 = expr.getChildren().get(1);
                 if (isNotRequired(c1) && isNotEq(c2)) {
                     String f1 = c1.getChildren().get(0).getField();
                     String f2 = c2.getChildren().get(0).getField();
                     if (f1 != null && f2 != null && (f1.equals(f2) || f2.startsWith(f1 + "."))) return c2;
                 }
                 if (isNotRequired(c2) && isNotEq(c1)) {
                     String f1 = c1.getChildren().get(0).getField();
                     String f2 = c2.getChildren().get(0).getField();
                     if (f1 != null && f2 != null && (f1.equals(f2) || f1.startsWith(f2 + "."))) return c1;
                 }
                 // Handle (X == null || X == 'null') patterns
                 if (isNotRequired(c1) && isNotRequired(c2)) {
                     String f1 = c1.getChildren().get(0).getField();
                     String f2 = c2.getChildren().get(0).getField();
                     if (f1 != null && f2 != null && f2.startsWith(f1 + ".")) return c2;
                     if (f1 != null && f2 != null && f1.startsWith(f2 + ".")) return c1;
                 }
            }

            // B. Greedy Factoring: Extract common terms from ANY number of branches
            // Use frequency map to find the most common term
            Map<String, Integer> counts = new HashMap<>();
            List<LogicExpressionVO> allAndChildren = expr.getChildren().stream()
                    .filter(c -> "AND".equals(c.getOperator()))
                    .toList();
            
            if (allAndChildren.size() == expr.getChildren().size()) {
                for (LogicExpressionVO and : allAndChildren) {
                    for (LogicExpressionVO child : and.getChildren()) {
                        counts.put(child.toString(), counts.getOrDefault(child.toString(), 0) + 1);
                    }
                }

                String bestTermStr = counts.entrySet().stream()
                        .filter(e -> e.getValue() > 1)
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(null);

                if (bestTermStr != null) {
                    LogicExpressionVO bestTerm = null;
                    // Find actual VO for this string
                    outer: for (LogicExpressionVO and : allAndChildren) {
                        for (LogicExpressionVO child : and.getChildren()) {
                            if (child.toString().equals(bestTermStr)) {
                                bestTerm = child;
                                break outer;
                            }
                        }
                    }

                    List<LogicExpressionVO> branchesWithTerm = new ArrayList<>();
                    List<LogicExpressionVO> branchesWithoutTerm = new ArrayList<>();
                    for (LogicExpressionVO and : allAndChildren) {
                        boolean hasTerm = and.getChildren().stream().anyMatch(c -> c.toString().equals(bestTermStr));
                        if (hasTerm) {
                            List<LogicExpressionVO> remaining = and.getChildren().stream()
                                    .filter(c -> !c.toString().equals(bestTermStr))
                                    .collect(Collectors.toList());
                            branchesWithTerm.add(simplify(new LogicExpressionVO("AND", null, null, remaining)));
                        } else {
                            branchesWithoutTerm.add(and);
                        }
                    }

                    LogicExpressionVO factoredPart = simplify(new LogicExpressionVO("AND", null, null, 
                        Arrays.asList(bestTerm, simplify(new LogicExpressionVO("OR", null, null, branchesWithTerm)))));
                    
                    if (branchesWithoutTerm.isEmpty()) return factoredPart;
                    
                    List<LogicExpressionVO> finalOrChildren = new ArrayList<>(branchesWithoutTerm);
                    finalOrChildren.add(factoredPart);
                    return simplify(new LogicExpressionVO("OR", null, null, finalOrChildren));
                }
            }
        }

        // 6. Unwrap single-child logic
        if (("AND".equals(expr.getOperator()) || "OR".equals(expr.getOperator())) && 
            expr.getChildren() != null && expr.getChildren().size() == 1) {
            return expr.getChildren().get(0);
        }

        return expr;
    }

    private boolean isNotRequired(LogicExpressionVO expr) {
        return "NOT".equals(expr.getOperator()) && 
               expr.getChildren() != null && 
               expr.getChildren().size() == 1 &&
               "REQUIRED".equals(expr.getChildren().get(0).getOperator());
    }

    private boolean isNotEq(LogicExpressionVO expr) {
        return "NOT".equals(expr.getOperator()) && 
               expr.getChildren() != null && 
               expr.getChildren().size() == 1 &&
               "EQ".equals(expr.getChildren().get(0).getOperator());
    }

    private List<LogicExpressionVO> parseLogicList(JsonNode listNode, String prefix) {
        List<LogicExpressionVO> result = new ArrayList<>();
        if (listNode.isArray()) {
            for (JsonNode item : listNode) {
                LogicExpressionVO child = parseLogic(item, prefix);
                if (child != null) result.add(child);
            }
        }
        return result;
    }

    private LogicExpressionVO parseFieldConstraint(String fieldId, JsonNode constraint) {
        if (constraint == null || constraint.isMissingNode()) return null;

        List<LogicExpressionVO> subExprs = new ArrayList<>();

        // Handle Amazon's deep value nesting: items -> properties -> value -> ...
        if (constraint.has("items")) {
            JsonNode items = constraint.get("items");
            if (items.has("properties") && items.get("properties").has("value")) {
                return parseFieldConstraint(fieldId, items.get("properties").get("value"));
            }
        }

        // Handle logical connectives inside a field constraint
        if (constraint.has("anyOf")) {
             List<LogicExpressionVO> children = new ArrayList<>();
             for (JsonNode sub : constraint.get("anyOf")) {
                 LogicExpressionVO child = parseFieldConstraint(fieldId, sub);
                 if (child != null) children.add(child);
             }
             if (!children.isEmpty()) subExprs.add(new LogicExpressionVO("OR", null, null, children));
        }
        if (constraint.has("allOf")) {
             List<LogicExpressionVO> children = new ArrayList<>();
             for (JsonNode sub : constraint.get("allOf")) {
                 LogicExpressionVO child = parseFieldConstraint(fieldId, sub);
                 if (child != null) children.add(child);
             }
             if (!children.isEmpty()) subExprs.add(new LogicExpressionVO("AND", null, null, children));
        }
        if (constraint.has("not")) {
            LogicExpressionVO child = parseFieldConstraint(fieldId, constraint.get("not"));
            if (child != null) subExprs.add(new LogicExpressionVO("NOT", null, null, Collections.singletonList(child)));
        }

        // Handle 'contains' - Amazon use this for arrays. We flatten it to .0 to match UI field IDs.
        if (constraint.has("contains")) {
            JsonNode contains = constraint.get("contains");
            if (hasDirectValueConstraint(contains)) {
                // Simple case: contains a string value
                subExprs.add(new LogicExpressionVO("EQ", fieldId + ".0", getDirectValue(contains), null));
            } else {
                // Complex case: contains a sub-schema. Flatten to .0 index.
                LogicExpressionVO innerLogic = parseLogic(contains, fieldId + ".0");
                if (innerLogic != null) {
                    return innerLogic; // Flattened result
                }
            }
        }

        // Handle direct value constraints
        if (constraint.has("enum")) {
            subExprs.add(new LogicExpressionVO("EQ", fieldId, constraint.get("enum").get(0).asText(), null));
        } else if (constraint.has("const")) {
            subExprs.add(new LogicExpressionVO("EQ", fieldId, constraint.get("const").asText(), null));
        }

        // Handle nested properties in object fields
        if (constraint.has("properties")) {
            Iterator<Map.Entry<String, JsonNode>> it = constraint.get("properties").fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                LogicExpressionVO sub = parseFieldConstraint(fieldId + "." + entry.getKey(), entry.getValue());
                if (sub != null) subExprs.add(sub);
            }
        }

        if (subExprs.isEmpty()) return null;
        LogicExpressionVO root = (subExprs.size() == 1) ? subExprs.get(0) : new LogicExpressionVO("AND", null, null, subExprs);
        return simplify(root);
    }

    private boolean hasDirectValueConstraint(JsonNode node) {
        return node.has("const") || node.has("enum");
    }

    private Object getDirectValue(JsonNode node) {
        if (node.has("const")) return node.get("const").asText();
        if (node.has("enum")) return node.get("enum").get(0).asText();
        return null;
    }

    private void addRulesToField(List<AmzListingFormFieldVO> fields, String targetId, LogicExpressionVO condition, String type, String action) {
        List<AmzListingFormFieldVO> targetFields = fields.stream()
                .filter(f -> f.getId().equals(targetId) || f.getId().startsWith(targetId + "."))
                .toList();

        for (AmzListingFormFieldVO targetField : targetFields) {
            AmzListingFieldRuleVO rule = new AmzListingFieldRuleVO();
            rule.setType(type);
            rule.setConditionLogic(condition);
            // Fallback for transition
            rule.setCondition(renderConditionString(condition));
            rule.setAction(action);
            targetField.getLinkageRules().add(rule);
        }
    }

    private String renderConditionString(LogicExpressionVO expr) {
        if (expr == null) return "";
        String op = expr.getOperator();
        
        if ("EQ".equals(op)) return String.format("%s == '%s'", expr.getField(), expr.getValue());
        if ("CONTAINS".equals(op)) {
            return String.format("%s.contains('%s')", expr.getField(), expr.getValue());
        }
        if ("REQUIRED".equals(op)) return String.format("%s != null", expr.getField());
        
        if ("NOT".equals(op)) {
            LogicExpressionVO child = expr.getChildren().get(0);
            if ("REQUIRED".equals(child.getOperator())) {
                return String.format("%s == null", child.getField());
            }
            if ("EQ".equals(child.getOperator())) {
                return String.format("%s != '%s'", child.getField(), child.getValue());
            }
            return "!(" + renderConditionString(child) + ")";
        }
        
        if ("AND".equals(op) || "OR".equals(op)) {
            String joiner = "AND".equals(op) ? " && " : " || ";
            return "(" + String.join(joiner, expr.getChildren().stream().map(this::renderConditionString).toList()) + ")";
        }
        return "";
    }

    private boolean isImportantField(String id) {
        if (IMPORTANT_FIELDS_WHITELIST.contains(id)) {
            return true;
        }
        // Also consider properties that contain these keywords as important (e.g. array items)
        for (String important : IMPORTANT_FIELDS_WHITELIST) {
            if (id.contains(important)) {
                return true;
            }
        }
        return false;
    }
}
