dependencies {
    // 内部模块
    api(project(":hzapp-module-erplus:hzapp-module-erplus-api"))

    // 外部模块
    api("com.hzltd:hzapp-module-infra")




    // 工具类
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("com.squareup.okhttp3:okhttp")
    implementation("org.threeten:threetenbp:1.3.5")

    // 测试
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
