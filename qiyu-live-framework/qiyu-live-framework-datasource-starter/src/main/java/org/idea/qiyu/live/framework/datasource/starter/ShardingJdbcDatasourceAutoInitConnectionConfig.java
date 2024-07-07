package org.idea.qiyu.live.framework.datasource.starter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * @Author :jianggq
 * @Date :2024/7/2
 * Description : 做成配置，实现通过参数来触发 Hikari 数据源的初始化
 */
@Configuration
public class ShardingJdbcDatasourceAutoInitConnectionConfig {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ShardingJdbcDatasourceAutoInitConnectionConfig.class);

    @Bean
    public ApplicationRunner runner(DataSource dataSource) {
        return args -> {
            LOGGER.info("dataSource: {}", dataSource);
            //手动触发下连接池的连接创建
            Connection connection = dataSource.getConnection();
        };
    }
}