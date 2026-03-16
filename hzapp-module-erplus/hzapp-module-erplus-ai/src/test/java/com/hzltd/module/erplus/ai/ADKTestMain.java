package com.hzltd.module.erplus.ai;

import com.google.adk.agents.LlmAgent;
import com.google.adk.models.OllamaLlm;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.runner.Runner;
import com.google.adk.sessions.Session;
import com.google.common.collect.Maps;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import io.reactivex.rxjava3.core.Maybe;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Slf4j
public class ADKTestMain {
    private static String userId = "user_123";
    private static String sessionId = "session_123";
    private static String appName = "mas";

    public static void main(String[] args) {
        OllamaLlm ollamaLlm = new OllamaLlm("qwen3.5:9b", "http://192.168.1.3:11434/v1");


        LlmAgent analiAgent = new LlmAgent.Builder()
                .name("analiAgent")
                .model(ollamaLlm)
                .instruction("你负责数据分析")
                .description("你负责数据分析")
                .build();


        LlmAgent spApiAgent = new LlmAgent.Builder()
                .name("spApiAgent")
                .model(ollamaLlm)
                .instruction("你负责修改ASIN的listing")
                .description("负责修改ASIN的listing")
                .build();

        LlmAgent adDataAgent = new LlmAgent.Builder()
                .name("adDataAgent")
                .model(ollamaLlm)
                .instruction("你负责修改获取广告数据")
                .description("负责修改获取广告数据")
                .build();


        LlmAgent adOptAgent = new LlmAgent.Builder()
                .name("adOptAgent")
                .model(ollamaLlm)
                .instruction("你负责调整广告策略")
                .description("负责修改获取广告数据")
                .build();




        LlmAgent rootAgent = new LlmAgent.Builder()
                .name("AmazonPlannerAgent")
                .model(ollamaLlm)
                .subAgents(analiAgent, spApiAgent, adDataAgent, adOptAgent)
//                .beforeModelCallback(( callbackContext, llmRequestBuilder) -> {
//                    log.info("LlmRequestBuilder: {}", JsonUtils.toJsonString(llmRequestBuilder));
//                    return Maybe.empty();
//                })
                .afterModelCallback((callbackContext, llmResponse) -> {
                    log.info("resp: {}", llmResponse.toJson());
                    return Maybe.empty();
                })
//                .beforeModelCallback((callbackContext, llmResponse) -> {
//
//                })
                .instruction("你需要将用户的工作拆解为具体的可执行步骤, 并调用工具来完成它, 并协调各个工具的调用关系")
                .build();


        Runner runner = new InMemoryRunner(rootAgent, appName);
        Session s = runner.sessionService().createSession(appName, userId, Maps.newHashMap(), sessionId).blockingGet();

        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {

            while(true) {
// "请为Asin=xxxxx制定一个新品运营计划, 品类是珠宝首饰DIY套件, 面向亚马逊北美站，产品定价中高端，价格区间 20～30美元，运营目标是在ACOS<0.4的情况下快速出销量"

                System.out.print("\nYou > ");

                Content content = Content.fromParts(Part.fromText(scanner.nextLine()));
                runner.runAsync(s.sessionKey(), content).blockingForEach(e -> {


                    if (e.actions().transferToAgent().isPresent()) {
                        log.info("[转移Agent] 来源:{},  目标：{}", e.author(), e.actions().transferToAgent().get());
                    }

                    if (MapUtils.isNotEmpty(e.actions().requestedToolConfirmations())) {
                        log.info("[ToolConfirmation] 来源：{},  confirms: {}", e.author(), e.actions().requestedToolConfirmations());
                    }

                    if (e.finalResponse()) {
                        log.info("[FinalResponse] resp: {}", e.content().get());
                    }



                    log.info("Event: {}", e);


                });


            }



        }


    }
}
