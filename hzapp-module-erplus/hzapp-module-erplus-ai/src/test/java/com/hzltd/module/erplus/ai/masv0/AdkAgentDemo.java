package com.hzltd.module.erplus.ai.masv0;

import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.models.OllamaLlm;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AdkAgentDemo {

    // 1. 定义常量，确保创建和运行时的一致性
    private static final String APP_NAME = "AmazonAssistant";
    private static final String USER_ID = "developer_001"; // 显式定义，避免 GlobalUser 漂移

    public static void main(String[] args) {
        try {
            // 2. 初始化 Ollama 模型 (连接到你的本地或服务器节点)
            OllamaLlm ollamaLlm = new OllamaLlm("qwen3.5:9b", "http://192.168.1.3:11434/v1");

            // 3. 构建 Agent：定义角色和能力
            LlmAgent amazonAgent = new LlmAgent.Builder()
                    .name("AmazonPlannerAgent")
                    .model(ollamaLlm)
                    .instruction("你是一个亚马逊店铺运营专家，擅长数据分析和广告优化。")
                    .build();

            // 4. 创建 Runner (InMemoryRunner 会在内存中维护 Session)
            InMemoryRunner runner = new InMemoryRunner(amazonAgent);

            // 5. 创建会话：这是最关键的一步，必须拿到 Session ID
            log.info("正在为用户 [{}] 创建会话...", USER_ID);
            Session session = runner.sessionService()
                    .createSession(APP_NAME, USER_ID)
                    .blockingGet();



            String sessionId = session.id();
            log.info("会话创建成功，ID: {}", sessionId);

            // 6. 准备用户消息
            Content userMsg = Content.fromParts(Part.fromText("请帮我写一个针对夏天冷萃咖啡瓶的新品推广计划。"));

            // 7. 运行 Agent 并监听事件流 (流式输出)
            log.info("开始执行 Agent 任务...");
            Flowable<Event> events = runner.runAsync(USER_ID, sessionId, userMsg);

            events.blockingForEach(event -> {
                // 根据事件类型处理输出（如文本块、完成信号等）
                System.out.println(event);
            });

            System.out.println("\n--- 任务执行完成 ---");

        } catch (Exception e) {
            log.error("运行出错: {}", e.getMessage(), e);
        }
    }
}