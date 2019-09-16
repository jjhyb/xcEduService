package com.xuecheng.govern.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * @author: huangyibo
 * @Date: 2019/9/7 19:57
 * @Description:
 */

@EnableEurekaServer//标识这是一个Eureka服务
@SpringBootApplication
public class GovernCenterApplication {

    public static void main(String[] args) {

        SpringApplication.run(GovernCenterApplication.class, args);
    }
}