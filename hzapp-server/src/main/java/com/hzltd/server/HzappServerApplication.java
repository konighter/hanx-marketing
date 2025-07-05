package com.hzltd.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * 项目的启动类
 *
 * 如果你碰到启动的问题，请认真阅读 https://help.h2z.ltd/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://help.h2z.ltd/quick-start/ 文章
 * 如果你碰到启动的问题，请认真阅读 https://help.h2z.ltd/quick-start/ 文章
 *
 * @author 翰展科技
 */

@SuppressWarnings("SpringComponentScan") // 忽略 IDEA 无法识别 ${hzapp.info.base-package}
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"${hzapp.info.base-package}.server", "${hzapp.info.base-package}.module"}, nameGenerator= UniqueNameGenerator.class)
public class HzappServerApplication {

    public static void main(String[] args) {
        // 如果你碰到启动的问题，请认真阅读 https://help.h2z.ltd/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://help.h2z.ltd/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://help.h2z.ltd/quick-start/ 文章

        SpringApplication.run(HzappServerApplication.class, args);
//        new SpringApplicationBuilder(HzappServerApplication.class)
//                .applicationStartup(new BufferingApplicationStartup(20480))
//                .run(args);

        // 如果你碰到启动的问题，请认真阅读 https://help.h2z.ltd/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://help.h2z.ltd/quick-start/ 文章
        // 如果你碰到启动的问题，请认真阅读 https://help.h2z.ltd/quick-start/ 文章
    }

}
