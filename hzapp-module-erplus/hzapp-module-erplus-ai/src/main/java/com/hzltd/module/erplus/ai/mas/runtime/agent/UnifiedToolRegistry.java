package com.hzltd.module.erplus.ai.mas.runtime.agent;

import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Registry that bridges Spring Beans and their @MasTool annotated methods to ADK Tools.
 */
@Slf4j
@Service
public class UnifiedToolRegistry {

    private final ApplicationContext applicationContext;
    private final ConcurrentMap<String, BaseTool> toolCache = new ConcurrentHashMap<>();

    public UnifiedToolRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Resolves a list of ADK BaseTool objects from a list of Spring bean names.
     * It scans each bean for methods annotated with @MasTool.
     */
    public List<BaseTool> resolveTools(List<String> beanNames) {
        if (beanNames == null || beanNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<BaseTool> resolvedTools = new ArrayList<>();
        for (String beanName : beanNames) {
            try {
                Object bean = applicationContext.getBean(beanName);
                resolvedTools.addAll(getToolsFromBean(bean));
            } catch (Exception e) {
                log.error("Failed to resolve tool bean: {}", beanName, e);
            }
        }
        return resolvedTools;
    }

    private List<BaseTool> getToolsFromBean(Object bean) {
        List<BaseTool> tools = new ArrayList<>();
        Class<?> clazz = bean.getClass();
        
        // Handle CGLIB proxies
        if (clazz.getName().contains("$$")) {
            clazz = clazz.getSuperclass();
        }

        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(MasTool.class)) {
                String toolKey = bean.getClass().getName() + "#" + method.getName();
                BaseTool tool = toolCache.computeIfAbsent(toolKey, k -> {
                    // FunctionTool.create will use reflection to map this method
                    // and its parameters to a FunctionDeclaration for the LLM.
                    return FunctionTool.create(bean, method);
                });
                tools.add(tool);
                log.debug("Registered tool: {} from bean {}", method.getName(), bean.getClass().getSimpleName());
            }
        }
        return tools;
    }
}
