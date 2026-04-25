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
public class AdsAdCreateRequest {
 
    private String campaignId;
 
    private String adGroupId;
 
    private String name;
 
    private String adFormat;
 
    private String status;
 
    private String headline;
 
    private String description;
 
    private String landingPageUrl;
 
    private String callToAction;
 
    @Builder.Default
    private Map<String, Object> attributes = Maps.newHashMap();
 
    public AdsAdCreateRequest add(String field, Object value) {
        attributes.put(field, value);
        return this;
    }
}
