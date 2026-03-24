package com.hzltd.module.amz.api.adv.model.event;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.OffsetDateTime;

@Slf4j
public class AmazonZonedDateTimeDeserializer extends JsonDeserializer<Long> {

    @Override
    public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String dateStr = p.getText();
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateStr);
            return offsetDateTime.toInstant().toEpochMilli();
        } catch (Exception e) {
            log.error("解析 OffsetDateTime 失败: {}", dateStr, e);
            return null;
        }
    }
}
