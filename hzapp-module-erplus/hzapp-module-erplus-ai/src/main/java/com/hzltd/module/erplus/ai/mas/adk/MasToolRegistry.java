package com.hzltd.module.erplus.ai.mas.adk;

import com.google.adk.tools.BaseTool;
import com.google.adk.tools.FunctionTool;
import com.hzltd.module.erplus.ai.mas.agent.MasTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Skill 级别的 Tool 注册表。
 * <p>
 * 管理所有 @MasTool 注解的方法，支持按 Skill requiredTools 和阶段(phase) 进行过滤加载。
 * </p>
 * <p>
 * Tool 分为两类:
 * <ul>
 *   <li>查询类 (query): collect / decide / review 阶段可用</li>
 *   <li>操作类 (action): 仅 execute 阶段可用</li>
 * </ul>
 * </p>
 */
@Slf4j
@Service
public class MasToolRegistry {

    private final ApplicationContext applicationContext;

    /** toolName -> BaseTool 缓存 */
    private final ConcurrentHashMap<String, BaseTool> toolCache = new ConcurrentHashMap<>();

    /** toolName -> category (query / action) */
    private final ConcurrentHashMap<String, String> toolCategoryMap = new ConcurrentHashMap<>();

    private volatile boolean initialized = false;

    public MasToolRegistry(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 根据 Skill 的 requiredTools 列表和当前阶段，返回过滤后的 Tool 集合。
     *
     * @param requiredToolNames Skill 声明的 Tool 名称列表
     * @param phase             当前阶段: collect / decide / execute / review
     * @return 过滤后的 Tool 列表
     */
    public List<BaseTool> getToolsForPhase(List<String> requiredToolNames, String phase) {
        ensureInitialized();

        if (requiredToolNames == null || requiredToolNames.isEmpty()) {
            return Collections.emptyList();
        }

        List<BaseTool> result = new ArrayList<>();
        for (String toolName : requiredToolNames) {
            BaseTool tool = toolCache.get(toolName);
            if (tool == null) {
                log.warn("[MasToolRegistry] Tool not found: {}", toolName);
                continue;
            }

            String category = toolCategoryMap.getOrDefault(toolName, "query");

            // 阶段过滤: action 类 Tool 仅在 execute 阶段暴露
            if ("action".equals(category) && !"execute".equals(phase)) {
                continue;
            }

            result.add(tool);
        }

        log.info("[MasToolRegistry] Phase '{}': loaded {} tools from {} requested",
                phase, result.size(), requiredToolNames.size());
        return result;
    }

    /**
     * 获取所有已注册的 Tool 名称
     */
    public Set<String> getAllToolNames() {
        ensureInitialized();
        return Collections.unmodifiableSet(toolCache.keySet());
    }

    /**
     * 扫描所有 Spring Bean，发现并注册 @MasTool 注解的方法。
     * 使用 @MasToolCategory 注解或方法所在类的包路径来判断 category。
     */
    private synchronized void ensureInitialized() {
        if (initialized) return;

        log.info("[MasToolRegistry] Scanning for @MasTool annotated methods...");
        String[] beanNames = applicationContext.getBeanDefinitionNames();

        for (String beanName : beanNames) {
            try {
                Object bean = applicationContext.getBean(beanName);
                Class<?> clazz = bean.getClass();

                // Handle CGLIB proxies
                if (clazz.getName().contains("$$")) {
                    clazz = clazz.getSuperclass();
                }

                for (Method method : clazz.getDeclaredMethods()) {
                    if (method.isAnnotationPresent(MasTool.class)) {
                        String toolName = method.getName();
                        BaseTool tool = FunctionTool.create(bean, method);
                        toolCache.put(toolName, tool);

                        // 通过包路径推断 category
                        String category = inferCategory(clazz);
                        toolCategoryMap.put(toolName, category);

                        log.info("[MasToolRegistry] Registered tool: {} [{}] from {}",
                                toolName, category, clazz.getSimpleName());
                    }
                }
            } catch (Exception e) {
                // Skip non-relevant beans
            }
        }

        log.info("[MasToolRegistry] Initialization complete. {} tools registered.", toolCache.size());
        initialized = true;
    }

    /**
     * 通过类的包路径推断 Tool 类别
     */
    private String inferCategory(Class<?> clazz) {
        String packageName = clazz.getPackage().getName();
        if (packageName.contains(".action")) {
            return "action";
        }
        return "query";
    }
}
