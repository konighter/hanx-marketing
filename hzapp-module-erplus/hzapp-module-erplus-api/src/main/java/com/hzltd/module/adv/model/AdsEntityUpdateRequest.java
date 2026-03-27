package com.hzltd.module.adv.model;

import com.google.common.collect.Maps;
import com.hzltd.module.adv.enums.AdsEntityTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AdsEntityUpdateRequest {

    private AdsEntityTypeEnum entityType;

    private String entityId;

    private Map<String, Object> data = Maps.newHashMap();

    AdsEntityUpdateRequest add(String field, Object value) {
        data.putIfAbsent(field, value);
        return this;
    }
}
