package com.hzltd.module.erplus.adv.model;

import com.google.common.collect.Maps;
import com.hzltd.module.erplus.adv.enums.AdsEntityTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class AdsEntityUpdateRequest {

    private AdsEntityTypeEnum entityType;

    private String entityId;
    @Builder.Default
    private Map<String, Object> data = Maps.newHashMap();

    AdsEntityUpdateRequest add(String field, Object value) {
        data.putIfAbsent(field, value);
        return this;
    }
}
