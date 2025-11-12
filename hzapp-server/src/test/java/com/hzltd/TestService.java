package com.hzltd;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.api.DashScopeImageApi;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.ai.image.ImageOptionsBuilder;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;

public class TestService {


    @Test
    public void test1() {
        DashScopeImageApi api = DashScopeImageApi.builder().apiKey("sk-95888012131e4f7ea1dd0d1c4c561423").build();


        DashScopeImageModel model = DashScopeImageModel.builder()
                .dashScopeApi(api)
                .retryTemplate(
                        RetryTemplate.builder().infiniteRetry()
                                .build()
                ).build();

        ImageResponse imageResponse = model.call(new ImagePrompt("可爱的小猫", ImageOptionsBuilder.builder()
//                .model("qwen-image-plus") wan2.5-t2i-preview 928*1664
                .model("qwen-image-plus")
                .width(928)
                .height(1664)
                .build()));
        System.out.println(JSON.toJSON(imageResponse));
        // {"metadata":{"rawMap":{"taskId":"a90e2a60-cdb1-4707-a659-103646734319","taskStatus":"TIMED_OUT"},"created":1762487565268,"empty":false},"results":[]}

    }

    @Test
    public void test3() {
        DashScopeImageApi api = DashScopeImageApi.builder().apiKey("sk-95888012131e4f7ea1dd0d1c4c561423").build();
//        DashScopeImageModel model = DashScopeImageModel.builder().dashScopeApi(api).defaultOptions().build();
        ResponseEntity<DashScopeImageApi.DashScopeImageAsyncResponse> responseEntity = api.getImageGenTaskResult("9d7a2876-406c-487f-872c-238d58c70092");
        System.out.println(JSON.toJSON(responseEntity.getBody()));
    }


}
