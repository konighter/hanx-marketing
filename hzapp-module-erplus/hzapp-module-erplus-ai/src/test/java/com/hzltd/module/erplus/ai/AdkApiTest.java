package com.hzltd.module.erplus.ai;
import com.google.adk.agents.CallbackContext;
import com.google.adk.agents.LlmAgent;
import com.google.adk.events.Event;
import com.google.adk.models.LlmRequest;
import com.google.adk.models.OllamaLlm;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.runner.Runner;
import com.google.adk.sessions.ListSessionsResponse;
import com.google.adk.sessions.Session;
import com.google.adk.tools.BaseTool;
import com.google.common.collect.Maps;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import com.hzltd.framework.common.util.json.JsonUtils;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

@Slf4j
public class AdkApiTest {
    private static String userId = "user_123";
    private static String sessionId = "session_123";
    private static String appName = "mas";
    @org.junit.jupiter.api.Test
    public void test() throws InterruptedException {







    }


@Test
    public void test2() throws InterruptedException {

        OllamaLlm ollamaLlm = new OllamaLlm("qwen3.5:9b", "http://192.168.1.3:11434/v1");

        LlmAgent ROOT_AGENT = new LlmAgent.Builder()
                .name("AmazonPlannerAgent")
                .model(ollamaLlm)
                .instruction("你是一个亚马逊店铺运营专家, 擅长各种维度的数据分析、商品运营和广告运营技巧")
                .build();

        InMemoryRunner runner = new InMemoryRunner(ROOT_AGENT);
        Session session =
                runner
                        .sessionService()
                        .createSession(appName, userId)
                        .blockingGet();


        Content userMsg = Content.fromParts(Part.fromText("请为Asin制定一个新品运营计划"));
        Flowable<Event> events = runner.runAsync(userId, session.id(), userMsg);

        events.blockingForEach(e -> {
            log.info("Event: {}", e);
        });


    TimeUnit.SECONDS.sleep(10000);

    }
}
