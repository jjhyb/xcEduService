package com.xuecheng.manage_course;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/9/7 23:51
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {

    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void testRibbon(){
        //确定要获取的服务名称
        String serviceId = "XC-SERVICE-MANAGE-CMS";
        //Ribbon客户端从eurekaserver中获取服务列表
        for(int i = 0;i<10;i++){
            ResponseEntity<Map> responseEntity = restTemplate.getForEntity("http://"+serviceId+"/cms/page/get/5a754adf6abb500ad05688d9", Map.class);
            Map map = responseEntity.getBody();
            System.out.println(map);
        }
    }
}
