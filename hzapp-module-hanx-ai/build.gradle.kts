dependencies {
    // 内部模块依赖
    implementation(project(":hzapp-module-erplus:hzapp-module-erplus-api"))

    // Spring AI 相关
    implementation("org.springframework.ai:spring-ai-client-chat:1.0.1")
    implementation("org.springframework.ai:spring-ai-starter-model-ollama:1.0.1")

    // Google ADK
    implementation("com.google.adk:google-adk:1.2.0")

    // 基础设施 Starter (由 BOM 管理版本)
    implementation("org.flowable:flowable-spring-boot-starter-process")
    implementation("org.flowable:flowable-spring-boot-starter-actuator")
    implementation("com.hzltd:hzapp-spring-boot-starter-mybatis")
    implementation("com.hzltd:hzapp-spring-boot-starter-job")
    implementation("com.hzltd:hzapp-spring-boot-starter-biz-tenant")

    // 校验相关
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")

    // 测试相关
    testImplementation("com.hzltd:hzapp-spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}
