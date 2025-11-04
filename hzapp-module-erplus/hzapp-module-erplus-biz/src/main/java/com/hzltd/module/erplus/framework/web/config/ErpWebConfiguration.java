package com.hzltd.module.erplus.framework.web.config;

import com.hzltd.framework.swagger.config.HzappSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * erp 模块的 web 组件的 Configuration
 *
 * @author 翰展科技
 */
@Configuration(proxyBeanMethods = false)
public class ErpWebConfiguration {

    /**
     * erp 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi erpGroupedOpenApi() {
        return HzappSwaggerAutoConfiguration.buildGroupedOpenApi("erp");
    }

}
