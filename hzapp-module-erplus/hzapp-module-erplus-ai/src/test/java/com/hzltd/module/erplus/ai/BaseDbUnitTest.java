package com.hzltd.module.erplus.ai;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.druid.spring.boot3.autoconfigure.DruidDataSourceAutoConfigure;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.github.yulichang.autoconfigure.MybatisPlusJoinAutoConfiguration;
import com.hzltd.framework.datasource.config.HzappDataSourceAutoConfiguration;
import com.hzltd.framework.mybatis.config.HzappMybatisAutoConfiguration;
import com.hzltd.framework.test.config.SqlInitializationTestConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;

@SpringBootTest(
    webEnvironment = WebEnvironment.NONE,
    classes = {BaseDbUnitTest.Application.class}
)
@ActiveProfiles({"unit-test"})
@Sql(
    scripts = {"/sql/clean.sql"},
    executionPhase = ExecutionPhase.AFTER_TEST_METHOD
)
public class BaseDbUnitTest {
    @Import({HzappDataSourceAutoConfiguration.class, DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class, DruidDataSourceAutoConfigure.class, SqlInitializationTestConfiguration.class, HzappMybatisAutoConfiguration.class, MybatisPlusAutoConfiguration.class, MybatisPlusJoinAutoConfiguration.class, SpringUtil.class})
    public static class Application {
    }
}
