package com.hzltd.module.erplus.ai.mas.adk;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Google ADK 集成 Demo 测试
 * 
 * 注意: 此测试展示 ADK 的使用模式和 API 结构
 * 实际运行需要添加 google-adk 依赖
 * 
 * Maven 依赖 (需要添加到 pom.xml):
 * <dependency>
 *     <groupId>com.google</groupId>
 *     <artifactId>google-adk</artifactId>
 *     <version>0.0.1-SNAPSHOT</version>
 * </dependency>
 */
@DisplayName("Google ADK 集成 Demo")
public class GoogleAdkDemoTest {

    /**
     * Demo 1: 定义一个简单的 Agent
     * 
     * ADK API 模式:
     * LlmAgent.builder()
     *     .name("agent-name")
     *     .description("描述")
     *     .instruction("系统提示词")
     *     .model("gemini-2.0-flash")
     *     .build()
     */
    @Test
    @DisplayName("创建基础 LlmAgent")
    void testCreateBasicAgent() {
        // 模拟 ADK Agent 配置
        Map<String, Object> agentConfig = new HashMap<>();
        agentConfig.put("name", "science-teacher");
        agentConfig.put("description", "A friendly science teacher");
        agentConfig.put("instruction", "You are a science teacher for teenagers.");
        agentConfig.put("model", "gemini-2.0-flash");
        
        assertEquals("science-teacher", agentConfig.get("name"));
        assertEquals("gemini-2.0-flash", agentConfig.get("model"));
        System.out.println("✓ Agent 配置创建成功: " + agentConfig.get("name"));
    }

    /**
     * Demo 2: 定义带 Tool 的 Agent
     * 
     * ADK API 模式:
     * LlmAgent.builder()
     *     .name("stock-agent")
     *     .tools(FunctionTool.create(StockTicker.class, "lookupStockTicker"))
     *     .build()
     */
    @Test
    @DisplayName("创建带 Tool 的 Agent")
    void testCreateAgentWithTool() {
        // 模拟 Tool 定义
        Map<String, Object> toolSchema = new HashMap<>();
        toolSchema.put("name", "lookup_stock_ticker");
        toolSchema.put("description", "Lookup stock price for a given company");
        toolSchema.put("parameters", List.of(
            Map.of("name", "ticker", "type", "string", "description", "Stock ticker symbol")
        ));
        
        Map<String, Object> agentConfig = new HashMap<>();
        agentConfig.put("name", "stock-agent");
        agentConfig.put("tools", List.of(toolSchema));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> tools = (List<Map<String, Object>>) agentConfig.get("tools");
        assertEquals(1, tools.size());
        assertEquals("lookup_stock_ticker", tools.get(0).get("name"));
        
        System.out.println("✓ 带 Tool 的 Agent 创建成功, tools: " + tools.size());
    }

    /**
     * Demo 3: 工作流 - SequentialAgent
     * 
     * ADK API 模式:
     * SequentialAgent.builder()
     *     .name("poet-and-translator")
     *     .subAgents(poetAgent, translatorAgent)
     *     .build()
     */
    @Test
    @DisplayName("创建顺序工作流")
    void testSequentialWorkflow() {
        // 定义子 Agent
        Map<String, Object> poetAgent = new HashMap<>();
        poetAgent.put("name", "poet-agent");
        poetAgent.put("outputKey", "poem");
        
        Map<String, Object> translatorAgent = new HashMap<>();
        translatorAgent.put("name", "translator-agent");
        translatorAgent.put("instruction", "Translate {poem} to French");
        
        // 定义顺序工作流
        Map<String, Object> workflow = new HashMap<>();
        workflow.put("type", "SequentialAgent");
        workflow.put("name", "poet-and-translator");
        workflow.put("subAgents", List.of(poetAgent, translatorAgent));
        
        assertEquals("SequentialAgent", workflow.get("type"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> agents = (List<Map<String, Object>>) workflow.get("subAgents");
        assertEquals(2, agents.size());
        
        System.out.println("✓ 顺序工作流创建成功, 包含 " + agents.size() + " 个子 Agent");
    }

    /**
     * Demo 4: 工作流 - ParallelAgent
     */
    @Test
    @DisplayName("创建并行工作流")
    void testParallelWorkflow() {
        Map<String, Object> profileAgent = Map.of("name", "profile-agent");
        Map<String, Object> newsAgent = Map.of("name", "news-agent");
        Map<String, Object> financeAgent = Map.of("name", "finance-agent");
        
        Map<String, Object> workflow = new HashMap<>();
        workflow.put("type", "ParallelAgent");
        workflow.put("name", "market-researcher");
        workflow.put("subAgents", List.of(profileAgent, newsAgent, financeAgent));
        
        assertEquals("ParallelAgent", workflow.get("type"));
        System.out.println("✓ 并行工作流创建成功");
    }

    /**
     * Demo 5: 工作流 - LoopAgent (迭代优化)
     */
    @Test
    @DisplayName("创建循环工作流")
    void testLoopWorkflow() {
        Map<String, Object> generator = Map.of("name", "code-generator", "outputKey", "generated_code");
        Map<String, Object> reviewer = Map.of(
            "name", "code-reviewer", 
            "outputKey", "feedback",
            "instruction", "Review {generated_code} and provide feedback"
        );
        
        Map<String, Object> workflow = new HashMap<>();
        workflow.put("type", "LoopAgent");
        workflow.put("name", "code-refiner");
        workflow.put("subAgents", List.of(generator, reviewer));
        workflow.put("maxIterations", 3);
        
        assertEquals(3, workflow.get("maxIterations"));
        System.out.println("✓ 循环工作流创建成功, 最大迭代: " + workflow.get("maxIterations"));
    }

    /**
     * Demo 6: 状态共享 - outputKey
     */
    @Test
    @DisplayName("Agent 间状态共享")
    void testSharedState() {
        // 模拟共享状态
        Map<String, String> sharedState = new HashMap<>();
        sharedState.put("poem", "Roses are red, violets are blue...");
        sharedState.put("translated_poem", "Les roses sont rouges...");
        
        // 模拟占位符替换
        String instruction = "Translate the following poem to French:\n{poem}";
        String rendered = instruction.replace("{poem}", sharedState.get("poem"));
        
        assertTrue(rendered.contains("Roses are red"));
        assertFalse(rendered.contains("{poem}"));
        System.out.println("✓ 状态共享正常工作");
    }

    /**
     * Demo 7: 适配现有 MasAgent
     * 
     * 展示如何将现有的 MasAgent 适配为 ADK Agent
     */
    @Test
    @DisplayName("适配现有 MasAgent")
    void testAdapterPattern() {
        // 模拟现有的 MasAgent
        class MockMasAgent {
            private final String role;
            private final String name;
            private final String systemPrompt;
            
            MockMasAgent(String role, String name, String systemPrompt) {
                this.role = role;
                this.name = name;
                this.systemPrompt = systemPrompt;
            }
            
            String getRole() { return role; }
            String getName() { return name; }
            String getSystemPrompt() { return systemPrompt; }
        }
        
        // 模拟适配器
        class MasAgentToAdkAdapter {
            Map<String, Object> adapt(MockMasAgent masAgent) {
                Map<String, Object> adkConfig = new HashMap<>();
                adkConfig.put("name", "adk_" + masAgent.getRole().toLowerCase());
                adkConfig.put("description", "Adapted from: " + masAgent.getName());
                adkConfig.put("instruction", masAgent.getSystemPrompt());
                adkConfig.put("model", "gemini-2.0-flash");
                return adkConfig;
            }
        }
        
        MockMasAgent manager = new MockMasAgent("MANAGER", "General_Manager", 
            "You are a project manager. Coordinate team activities.");
        
        MasAgentToAdkAdapter adapter = new MasAgentToAdkAdapter();
        Map<String, Object> adkConfig = adapter.adapt(manager);
        
        assertEquals("adk_manager", adkConfig.get("name"));
        assertTrue(((String)adkConfig.get("description")).contains("General_Manager"));
        
        System.out.println("✓ MasAgent 适配成功: " + adkConfig.get("name"));
    }

    /**
     * Demo 8: 多 Agent 协作场景
     * 
     * 模拟 MAS 系统的多角色协作
     */
    @Test
    @DisplayName("多 Agent 协作场景")
    void testMultiAgentCollaboration() {
        // 定义角色 Agent
        List<Map<String, Object>> roles = List.of(
            Map.of("role", "MANAGER", "name", "ManagerAgent", "priority", 1),
            Map.of("role", "PM", "name", "PmAgent", "priority", 2),
            Map.of("role", "EXPERT", "name", "ExpertAgent", "priority", 3),
            Map.of("role", "EXECUTOR", "name", "ExecutorAgent", "priority", 4),
            Map.of("role", "REVIEWER", "name", "ReviewerAgent", "priority", 5)
        );
        
        // 使用 ParallelAgent 并行执行独立任务
        Map<String, Object> workflow = new HashMap<>();
        workflow.put("name", "mas-collaboration");
        workflow.put("type", "SequentialAgent");
        workflow.put("subAgents", List.of(
            Map.of("name", "planner", "type", "LlmAgent", "subAgents", roles.subList(0, 2)),
            Map.of("name", "executors", "type", "ParallelAgent", "subAgents", roles.subList(2, 4)),
            Map.of("name", "reviewer", "type", "LlmAgent")
        ));
        
        assertNotNull(workflow.get("subAgents"));
        System.out.println("✓ 多 Agent 协作工作流创建成功");
    }

    /**
     * Demo 9: 工具调用模拟
     */
    @Test
    @DisplayName("模拟 Tool 调用")
    void testToolInvocation() {
        // 模拟 Tool 定义
        Map<String, Object> toolDef = new HashMap<>();
        toolDef.put("name", "get_current_time");
        toolDef.put("description", "Get the current time");
        
        // 模拟 LLM 决定调用 Tool
        class ToolCall {
            String name;
            Map<String, Object> args;
            ToolCall(String name, Map<String, Object> args) {
                this.name = name;
                this.args = args;
            }
        }
        
        AtomicReference<String> result = new AtomicReference<>();
        
        // 模拟 Tool 执行
        class MockToolExecutor {
            String execute(ToolCall call) {
                if ("get_current_time".equals(call.name)) {
                    return "{\"time\": \"2025-01-01 12:00:00\"}";
                }
                return "{\"error\": \"Unknown tool\"}";
            }
        }
        
        ToolCall call = new ToolCall("get_current_time", new HashMap<>());
        String output = new MockToolExecutor().execute(call);
        
        assertTrue(output.contains("time"));
        System.out.println("✓ Tool 调用模拟成功: " + output);
    }

    /**
     * Demo 10: 完整场景 - 需求分析到代码生成
     */
    @Test
    @DisplayName("完整场景: 需求分析工作流")
    void testCompleteScenario() {
        // 1. PM Agent 分析需求
        Map<String, Object> pmTask = Map.of(
            "name", "pm-task",
            "instruction", "分析需求并拆解任务",
            "outputKey", "requirements"
        );
        
        // 2. Expert Agent 评审
        Map<String, Object> expertTask = Map.of(
            "name", "expert-review",
            "instruction", "评审需求: {requirements}",
            "outputKey", "review_result"
        );
        
        // 3. Executor Agent 生成代码
        Map<String, Object> executorTask = Map.of(
            "name", "code-generation",
            "instruction", "根据需求生成代码: {requirements}",
            "outputKey", "generated_code"
        );
        
        // 4. Reviewer Agent 审核
        Map<String, Object> reviewerTask = Map.of(
            "name", "code-review",
            "instruction", "审核代码: {generated_code}",
            "outputKey", "review_result"
        );
        
        // 组装工作流
        Map<String, Object> workflow = Map.of(
            "name", "requirement-to-code",
            "type", "SequentialAgent",
            "subAgents", List.of(pmTask, expertTask, executorTask, reviewerTask)
        );
        
        assertNotNull(workflow.get("subAgents"));
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> agents = (List<Map<String, Object>>) workflow.get("subAgents");
        
        System.out.println("✓ 完整工作流创建成功, 包含 " + agents.size() + " 个阶段");
        System.out.println("  1. PM 分析需求");
        System.out.println("  2. Expert 评审");
        System.out.println("  3. Executor 生成代码");
        System.out.println("  4. Reviewer 审核");
    }
}
