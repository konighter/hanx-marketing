package com.hzltd.module.erplus.adv.model;
 

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.hzltd.framework.common.util.json.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.MapUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
 
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdsAdGroupCreateRequest {
 
    private String campaignId;
 
    private String name;
 
    private String status;
 
    private Double defaultBid;
 
    private Map<String, Object> attributes;
 
    @Builder.Default
    private Map<String, Object> data = Maps.newHashMap();
 
    public AdsAdGroupCreateRequest add(String field, Object value) {
        data.put(field, value);
        return this;
    }

    public boolean containsAttribute(String name) {
        return MapUtils.isNotEmpty(attributes) && attributes.containsKey(name);
    }

    public <T> T getAttribute(String name, Class<T> clazz) {
        return JsonUtils.parseObject(JsonUtils.toJsonString(this.attributes.get(name)), clazz);
    }

    public <T> T getAttribute(String name, TypeReference<T> clazz) {
        return JsonUtils.parseObject(JsonUtils.toJsonString(this.attributes.get(name)), clazz);
    }

}
