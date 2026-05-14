plugins {
    id("org.springframework.boot")
}

dependencies {
    // 业务子项目引用（如果是本项目源码中的模块，请使用 project()）
    
    // 本项目内部模块
    // 注意：hzapp-module-erplus    // 内部业务模块
    implementation("com.hzltd:hzapp-module-hanx-ai")
    implementation(project(":hzapp-module-erplus:hzapp-module-erplus-api"))
    implementation(project(":hzapp-module-erplus:hzapp-module-erplus-biz"))
    implementation(project(":hzapp-module-erplus:hzapp-module-erplus-adv"))

    // 外部业务模块 (由根项目的 dependencyManagement 管理版本)
    implementation("com.hzltd:hzapp-module-system")
    implementation("com.hzltd:hzapp-module-infra")


    // Web 容器切换：Tomcat -> Jetty
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")

    // Spring Boot 配置处理器
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    
    // 其他启动器
    implementation("com.hzltd:hzapp-spring-boot-starter-protection")
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("${project.name}.jar")
}
