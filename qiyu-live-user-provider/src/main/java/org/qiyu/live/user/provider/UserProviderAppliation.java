package org.qiyu.live.user.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author :jianggq
 * @Date :2024/6/25
 * Description : 用户中台服务提供者
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class UserProviderAppliation {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(UserProviderAppliation.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
