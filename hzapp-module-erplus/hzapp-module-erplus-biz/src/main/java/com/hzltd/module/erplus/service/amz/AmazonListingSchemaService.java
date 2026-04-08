package com.hzltd.module.erplus.service.amz;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.hzltd.module.erplus.controller.admin.listing.vo.AmzListingFieldRuleVO;
import com.hzltd.module.erplus.controller.admin.listing.vo.AmzListingFormConfigVO;
import com.hzltd.module.erplus.controller.admin.listing.vo.AmzListingFormFieldVO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryAttributeDO;
import com.hzltd.module.erplus.dal.dataobject.cross.CrossMetaCategoryDO;
import com.hzltd.module.erplus.dal.mysql.category.CrossMetaCategoryAttributeMapper;
import com.hzltd.module.erplus.dal.mysql.category.CrossMetaCategoryMapper;
import com.hzltd.module.erplus.system.enums.CrossPlatformEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private CrossMetaCategoryMapper metaCategoryMapper;

    /**
     * Generates a VO configuration for a specific product type.
     */
    public AmzListingFormConfigVO generateFormConfig(String productType) {

        CrossMetaCategoryDO crossMetaCategoryDO = metaCategoryMapper.selectOne(new LambdaQueryWrapper<CrossMetaCategoryDO>()
                .eq(CrossMetaCategoryDO::getPlatformId, CrossPlatformEnum.AMAZON.getValue())
                .eq(CrossMetaCategoryDO::getCategoryCode, productType));

        if (crossMetaCategoryDO == null || StrUtil.isEmpty(crossMetaCategoryDO.getExtra())) {
            log.warn("[generateFormConfig] No schema found for productType: {}", productType);
            return null;
        }

        JsonNode schemaRoot = JsonUtils.parseTree(crossMetaCategoryDO.getExtra());
        AmzListingFormConfigVO config = new AmzListingFormConfigVO();
        config.setProductType(productType);
        
        List<AmzListingFormFieldVO> fields = new ArrayList<>();
        
        // 1. Recursive Parse Properties
        flattenProperties("", schemaRoot, fields, true);

        // 2. Parse Linkage Rules (allOf)
        parseLinkageRules(schemaRoot, fields);

        config.setFields(fields);
        return config;
    }

    private void flattenProperties(String prefix, JsonNode node, List<AmzListingFormFieldVO> fields, boolean parentIsRequired) {
        JsonNode properties = node.get("properties");
        if (properties == null || !properties.isObject()) return;

        Set<String> requiredAtLevel = extractRequiredAtLevel(node);
        
        Iterator<Map.Entry<String, JsonNode>> it = properties.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = it.next();
            String propName = entry.getKey();
            JsonNode propSchema = entry.getValue();
            String fullId = StrUtil.isEmpty(prefix) ? propName : prefix + "." + propName;

            // Skip common internal Amazon meta fields if they are known to be handled by context
            if (isInternalMetaField(propName)) continue;

            // Skip ERP master fields that are strictly managed globally
            if (isERPMasterField(propName)) continue;

            // Effective requirement = parent is required AND this property is required at this level
            boolean isRequiredThisLevel = requiredAtLevel.contains(propName);
            boolean isRequired = parentIsRequired && isRequiredThisLevel;
            
            String type = propSchema.path("type").asText("string");
            
            // Special Case: Array wrapping
            if ("array".equals(type) && propSchema.has("items")) {
                JsonNode items = propSchema.get("items");
                if (items.has("properties")) {
                    // Recurse into items for complex objects, using .0 to indicate the first item of the array
                    flattenProperties(fullId + ".0", items, fields, isRequired);
                } else {
                    // Simple array (list of values)
                    fields.add(parseField(fullId, propSchema, isRequired));
                }
            } else if ("object".equals(type) && propSchema.has("properties")) {
                // Nested object pattern
                flattenProperties(fullId, propSchema, fields, isRequired);
            } else {
                // Leaf node
                fields.add(parseField(fullId, propSchema, isRequired));
            }
        }
    }

    private boolean isInternalMetaField(String name) {
        return name.equals("marketplace_id") || name.equals("language_tag");
    }

    private boolean isERPMasterField(String name) {
        return name.equals("item_name") ||
                name.equals("brand") ||
                name.equals("manufacturer") ||
                name.equals("part_number") ||
                name.equals("model_number") ||
                name.equals("model_name") ||
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
        if (node.has("enum")) {
            builder.type("enum");
            builder.options(extractOptions(node));
        } else if (node.has("anyOf")) {
            // Check for complex enums in anyOf
            List<AmzListingFormFieldVO.Option> options = new ArrayList<>();
            for (JsonNode sub : node.get("anyOf")) {
                if (sub.has("enum")) {
                    options.addAll(extractOptions(sub));
                }
            }
            if (!options.isEmpty()) {
                builder.type("enum");
                builder.options(options);
            } else {
                builder.type("string"); // fallback
            }
    } else {
            String type = node.path("type").asText("string");
            String format = node.path("format").asText("");
            
            // Check for date format
            if ("date".equals(format) || "date-time".equals(format)) {
                builder.type("date");
            } else if (node.has("oneOf")) {
                // Check if any schema in oneOf specifies a date format
                boolean isDate = false;
                for (JsonNode sub : node.get("oneOf")) {
                    if ("date".equals(sub.path("format").asText("")) || 
                        "date-time".equals(sub.path("format").asText(""))) {
                        isDate = true;
                        break;
                    }
                }
                if (isDate) {
                    builder.type("date");
                } else {
                    builder.type(type);
                }
            } else {
                builder.type(type);
            }
        }
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
        // Simple implementation: extract dependency from 'if'
        // Format of if: { "properties": { "fieldA": { "enum": ["valueX"] } } }
        Map<String, String> conditions = new HashMap<>();
        JsonNode ifProps = ifNode.path("properties");
        if (ifProps.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> it = ifProps.fields();
            while (it.hasNext()) {
                Map.Entry<String, JsonNode> entry = it.next();
                String sourceField = entry.getKey();
                JsonNode constraints = entry.getValue();
                
                // Reach deep into value if needed (handling Amazon's nested pattern in IF)
                JsonNode valueConstraint = constraints.path("items").path("properties").path("value");
                if (valueConstraint.isMissingNode()) {
                    valueConstraint = constraints;
                }

                if (valueConstraint.has("enum")) {
                    conditions.put(sourceField, valueConstraint.get("enum").get(0).asText());
                } else if (valueConstraint.has("const")) {
                    conditions.put(sourceField, valueConstraint.get("const").asText());
                }
            }
        }

        if (conditions.isEmpty()) return;

        // Process 'then' effects
        // 1. Required fields
        JsonNode reqNode = thenNode.get("required");
        if (reqNode != null && reqNode.isArray()) {
            for (JsonNode targetFieldNode : reqNode) {
                String targetFieldId = targetFieldNode.asText();
                addRulesToField(fields, targetFieldId, conditions, "requirement", "required");
            }
        }

        // 2. Visibility / Editable (Amazon sometimes hides fields this way)
        JsonNode thenProps = thenNode.path("properties");
        if (thenProps.isObject()) {
            thenProps.fields().forEachRemaining(entry -> {
                String targetFieldId = entry.getKey();
                JsonNode meta = entry.getValue();
                if (meta.has("hidden")) {
                    String action = meta.get("hidden").asBoolean() ? "hide" : "show";
                    addRulesToField(fields, targetFieldId, conditions, "visibility", action);
                }
            });
        }
    }

    private void addRulesToField(List<AmzListingFormFieldVO> fields, String targetId, Map<String, String> conditions, String type, String action) {
        List<AmzListingFormFieldVO> targetFields = fields.stream()
                .filter(f -> f.getId().equals(targetId) || f.getId().startsWith(targetId + "."))
                .toList();
        
        for (AmzListingFormFieldVO targetField : targetFields) {
            for (Map.Entry<String, String> cond : conditions.entrySet()) {
                AmzListingFieldRuleVO rule = new AmzListingFieldRuleVO();
                rule.setType(type);
                // Condition format: field == 'value'
                rule.setCondition(String.format("%s == '%s'", cond.getKey(), cond.getValue()));
                rule.setAction(action);
                targetField.getLinkageRules().add(rule);
            }
        }
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
