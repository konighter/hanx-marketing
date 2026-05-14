dependencies {
    // 内部模块
    implementation(project(":hzapp-module-hanx-ai"))
    implementation(project(":hzapp-module-erplus:hzapp-module-erplus-api"))

    // 外部业务模块
    implementation("com.hzltd:hzapp-module-system")



    // 基础设施 Starter (由 BOM 管理版本)
    implementation("com.hzltd:hzapp-spring-boot-starter-web")
    implementation("com.hzltd:hzapp-spring-boot-starter-security")
    implementation("com.hzltd:hzapp-spring-boot-starter-mybatis")
    implementation("com.hzltd:hzapp-spring-boot-starter-redis")

    // 工具类
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("org.openapitools:jackson-databind-nullable:0.2.6")
    implementation("jakarta.annotation:jakarta.annotation-api")

    // AWS 相关
    implementation("io.awspring.cloud:spring-cloud-aws-starter-sqs")
    
    // 工作流
    implementation("org.flowable:flowable-spring-boot-starter-process")
    implementation("org.flowable:flowable-spring-boot-starter-actuator")


    // 兼容旧代码的 javax 依赖
    implementation("javax.validation:validation-api")
    implementation("javax.annotation:javax.annotation-api")

    // 测试相关
    testImplementation("com.hzltd:hzapp-spring-boot-starter-test")
}
