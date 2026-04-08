package com.hzltd.module.amz.spapi.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hzltd.framework.common.util.json.JsonUtils;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Amazon Listings 模式校验器
 * 基于 JSON Schema (Draft 7) 进行校验
 */
@Slf4j
@Component
public class ListingSchemaValidator {

    private final JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据指定的 JSON Schema 校验数据
     *
     * @param schemaContent Amazon 提供的 JSON Schema 字符串
     * @param dataContent   待校验的商品属性 JSON 字符串
     * @return 错误消息列表
     */
    public List<String> validate(String schemaContent, String dataContent) {
        try {
            JsonSchema schema = factory.getSchema(schemaContent);
            JsonNode node = objectMapper.readTree(dataContent);

            Set<ValidationMessage> errors = schema.validate(node);
            return errors.stream()
                    .map(ValidationMessage::getMessage)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("[validate][校验异常]", e);
            return java.util.Collections.singletonList("Schema validation error: " + e.getMessage());
        }
    }

    /**
     * 校验 Map 格式的数据
     */
    public List<String> validate(String schemaContent, Object data) {
        return validate(schemaContent, JsonUtils.toJsonString(data));
    }
}
