dependencies {
    // 核心公共包
    api("com.hzltd:hzapp-common")
    api("com.google.guava:guava")
    api("org.apache.commons:commons-collections4:4.4")
    api("org.apache.commons:commons-lang3")
    api("com.fasterxml.jackson.core:jackson-databind")


    // 飞书 SDK
    implementation("com.larksuite.oapi:oapi-sdk:2.6.1")

    // 业务相关
    api("org.springframework.boot:spring-boot-starter-validation")
    api("com.hzltd:hzapp-spring-boot-starter-biz-tenant")


    // 测试
    testImplementation("org.junit.jupiter:junit-jupiter")
}
