package com.xuecheng.test.freemarker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author: huangyibo
 * @Date: 2019/9/4 1:29
 * @Description:
 */

@SpringBootApplication
public class FreemarkerTestApplication {

    public static void main(String[] args) {

        SpringApplication.run(FreemarkerTestApplication.class,args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(new OkHttp3ClientHttpRequestFactory());
    }
}
