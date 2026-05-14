dependencies {
    // 内部模块
    implementation(project(":hzapp-module-erplus:hzapp-module-erplus-api"))
    implementation(project(":hzapp-module-erplus:hzapp-module-erplus-spapi"))
    implementation(project(":hzapp-module-erplus:hzapp-module-erplus-adv"))
    implementation(project(":hzapp-module-erplus:hzapp-module-erplus-amz:hzapp-module-erplus-amz-api"))

    // 远程调用 & 浏览器自动化
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:3.1.9")
    implementation("com.microsoft.playwright:playwright:1.55.0")
    implementation("io.github.kihdev:playwright-stealth-4j:1.1.2")
    implementation("com.squareup.okhttp3:okhttp")
    
    // 兼容旧代码的 javax 依赖
    implementation("javax.validation:validation-api")
    implementation("javax.annotation:javax.annotation-api")

    // 外部 SDK
    implementation("software.amazon.spapi:spapi-sdk:1.6.0")
    implementation("cn.hutool:hutool-all")
    
    // 工具类
    implementation("com.github.joschi.jackson:jackson-datatype-threetenbp:2.18.2")
    implementation("com.networknt:json-schema-validator:1.0.87")
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")

    // 测试
    testImplementation("com.hzltd:hzapp-spring-boot-starter-test")
}
