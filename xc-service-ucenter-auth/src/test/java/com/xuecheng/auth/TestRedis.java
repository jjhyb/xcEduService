package com.xuecheng.auth;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: huangyibo
 * @Date: 2019/9/13 19:03
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    private RedisTemplate redisTemplate;

    //创建JWT令牌
    @Test
    public void testRedis(){
        //定义key
        String key = "user_token:d5db318d-ddd3-4db0-b765-4d0fa64f08c0";
        //定义value
        Map<String,String> valueMap = new HashMap<>();
        valueMap.put("access_token","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU2ODQyODAyNiwianRpIjoiZDVkYjMxOGQtZGRkMy00ZGIwLWI3NjUtNGQwZmE2NGYwOGMwIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.GfW4m5klMKQMWmZUyxsn9XbuD3ofUzTLC1PxIUrD4DZJ9ycL-gjarCMeQN-B_26KocnT2QRZAzspQrfxgQKM095rnYwZ8oBCTulvOnHaP1guycKSkVQl56cCjUMgNu0KF-Yv-m74HsJY-UEHV8wv3U0IaGuMTJHuB9EcEOCIR_y6fewOK5Fpr-eTTEpRcbWASSBBhvyr4XSA_m4xLWaO24H9Var3p-BSspp70dd2ZmABd-nY-JThqln4UznVaPiM6YyCYN-NcD-xuLHHHKt5JvQRdgqeLt8fr6vJB6J89FUmOZEQZVC6wi3Khx7w6AxA8EPB1lkLDoenB7Ul8p6kWg");
        valueMap.put("refresh_token","eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiJkNWRiMzE4ZC1kZGQzLTRkYjAtYjc2NS00ZDBmYTY0ZjA4YzAiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU2ODQyODAyNiwianRpIjoiYTJjZDE3OWEtOTFlNi00YzZlLWE2NzAtYjk1YjE1MDAzNjFmIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.UTCXtms5xZ5SC6_5nZj7mElpkJwLoKtTrxrHSBradF6wVjQ0PCw3Tswv7hRZEltB8s6k0Cyqi_glmOBp-TbFCmxp67_hNSr4j_KeKXV7TmPhw27XhH-wOwOnH5oj0H3hKIj-7Xaepql7nsQ8ojlX6P-jk2GyXv2BImSk3cJicZf1hJz969YYGT3-i9bC-2AF9PfGSSSVwzrMgEyJEeqni01vOR4XhUmvpRWQXvBLsXzsN65LEUPOMqEQyY2nLWKvbpT6htsx1TvDp9aaHoz3jFInEV04aA5UHa-tfodacA1_ZBnuyYbN8MSFREUIixwJwMeox-hW7Hiny1tBJMipoA");
        String jsonString = JSON.toJSONString(valueMap);
        //存储数据
        redisTemplate.boundValueOps(key).set(jsonString,30,TimeUnit.SECONDS);

        //校验key是否存在，如果不存在直接返回-2
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        System.out.println(expire);

        //获取数据
        String tokenString = (String)redisTemplate.boundValueOps(key).get();
        System.out.println(tokenString);
    }

}
