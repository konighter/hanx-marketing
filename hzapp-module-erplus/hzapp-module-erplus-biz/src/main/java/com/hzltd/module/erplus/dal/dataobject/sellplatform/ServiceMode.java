package com.hzltd.module.erplus.dal.dataobject.sellplatform;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.hzltd.framework.common.util.json.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * 服务模式
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceMode {

    private String name;
    private String code;




    @MappedJdbcTypes({JdbcType.VARCHAR, JdbcType.CHAR, JdbcType.LONGNVARCHAR})
    @MappedTypes(List.class)
    public static class ServiceModeHandler extends AbstractJsonTypeHandler<List<ServiceMode>> {

        public ServiceModeHandler(Class<?> type) {
            super(type);
        }

        @Override
        public List<ServiceMode> parse(String json) {
            return JsonUtils.parseArray(json, ServiceMode.class);
        }

        @Override
        public String toJson(List<ServiceMode> obj) {
            return JsonUtils.toJsonString(obj);
        }

    }
}
