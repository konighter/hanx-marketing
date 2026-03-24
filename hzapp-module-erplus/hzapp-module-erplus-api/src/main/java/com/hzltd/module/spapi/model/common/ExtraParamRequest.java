package com.hzltd.module.spapi.model.common;

import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Data
public class ExtraParamRequest {

    private Map<String, Object> extraParam;


    public Object getParam(String param) {
        if (CollectionUtils.isEmpty(extraParam)) {
            return null;
        }
        return extraParam.get(param);
    }

    public void putParam(String param, Object val) {
        if (extraParam == null) {
            extraParam = Maps.newHashMap();
        }
        extraParam.put(param, val);
    }


}
