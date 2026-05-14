dependencies {
    // 内部模块
    api(project(":hzapp-module-erplus:hzapp-module-erplus-api"))
    api(project(":hzapp-module-erplus:hzapp-module-erplus-spapi"))

    // 外部业务模块 (由根项目的 dependencyManagement 管理版本)
    api("com.hzltd:hzapp-module-system")
    api("com.hzltd:hzapp-module-infra")
    
    // 兼容旧代码的 javax 依赖
    implementation("javax.validation:validation-api")
    implementation("javax.annotation:javax.annotation-api")

    api(project(":hzapp-module-erplus:hzapp-module-erplus-amz:hzapp-module-erplus-amz-biz"))




    // 工作流
    implementation("org.flowable:flowable-spring-boot-starter-process")
    implementation("org.flowable:flowable-spring-boot-starter-actuator")

    // 基础设施 Starter (由 BOM 管理版本)
    implementation("com.hzltd:hzapp-spring-boot-starter-web")
    implementation("com.hzltd:hzapp-spring-boot-starter-security")
    implementation("com.hzltd:hzapp-spring-boot-starter-mybatis")
    implementation("com.hzltd:hzapp-spring-boot-starter-redis")
    implementation("com.hzltd:hzapp-spring-boot-starter-excel")

    // 远程调用 & 浏览器自动化
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.1.9")
    implementation("com.microsoft.playwright:playwright:1.55.0")
    implementation("io.github.kihdev:playwright-stealth-4j:1.1.2")
    implementation("com.squareup.okhttp3:okhttp")

    // 工具类
    implementation("joda-time:joda-time:2.12.7")
    implementation("com.belerweb:pinyin4j:2.5.1")

    // 测试相关
    testImplementation("com.hzltd:hzapp-spring-boot-starter-test")
}
