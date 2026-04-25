package com.hzltd.module.erplus.adv.model;
 
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
 
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
}
