plugins {
    id("org.springframework.boot") version "3.5.5" apply false
    id("io.spring.dependency-management") version "1.1.7"
    java  // subprojects DSL 中使用 implementation/annotationProcessor 需要此插件
}

allprojects {
    group = "com.hzltd"
    version = project.property("revision") as String

    repositories {
        maven { 
            url = uri("https://packages.aliyun.com/6188d705e84c82e792917a32/maven/2153565-snapshot-kpytvb")
            credentials {
                username = System.getenv("MAVEN_USER") ?: project.findProperty("mavenUser") as String?
                password = System.getenv("MAVEN_PASSWORD") ?: project.findProperty("mavenPassword") as String?
            }
        }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://mirrors.huaweicloud.com/repository/maven/") }
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java-library")

    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    // 引入远程 BOM (相当于 Maven 的 dependencyManagement import)
    dependencyManagement {
        imports {
            mavenBom("com.hzltd:hzapp-dependencies:${project.version}")
            mavenBom("io.awspring.cloud:spring-cloud-aws-dependencies:3.4.2")
            mavenBom("software.amazon.awssdk:bom:2.31.78")
        }
        dependencies {
            dependency("com.hzltd:hzapp-module-hanx-ai:${project.version}")
            dependency("com.hzltd:hzapp-module-erplus-api:${project.version}")
            dependency("com.hzltd:hzapp-module-erplus-biz:${project.version}")
            dependency("com.hzltd:hzapp-module-erplus-spapi:${project.version}")
            dependency("com.hzltd:hzapp-module-erplus-adv:${project.version}")
            dependency("com.hzltd:hzapp-module-erplus-amz-api:${project.version}")
            dependency("com.hzltd:hzapp-module-erplus-amz-biz:${project.version}")
            dependency("com.hzltd:hzapp-module-system:${project.version}")
            dependency("com.hzltd:hzapp-module-infra:${project.version}")
            
            // 外部常用工具包
            dependency("com.squareup.okhttp3:okhttp:4.12.0")
            
            // 兼容性包：支持旧代码中的 javax.validation 和 javax.annotation
            dependency("javax.validation:validation-api:2.0.1.Final")
            dependency("javax.annotation:javax.annotation-api:1.3.2")
        }
    }




    dependencies {
        // 通用依赖：Lombok & MapStruct
        compileOnly("org.projectlombok:lombok:${project.property("lombokVersion")}")
        annotationProcessor("org.projectlombok:lombok:${project.property("lombokVersion")}")
        
        implementation("org.mapstruct:mapstruct:${project.property("mapstructVersion")}")
        annotationProcessor("org.mapstruct:mapstruct-processor:${project.property("mapstructVersion")}")
        annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
