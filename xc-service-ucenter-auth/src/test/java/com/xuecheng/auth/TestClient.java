package com.xuecheng.auth;

import com.xuecheng.framework.client.XcServiceList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Base64Utils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/9/13 19:03
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    /**
     * 远程请求SpringSecurity获取令牌
     */
    @Test
    public void testClient(){
        //从Eureka中获取认证服务的地址，因为Spring Security Oauth2在认证服务中
        //从Eureka中获取认证服务的一个实例地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //此地址就是http://ip:port
        URI uri = serviceInstance.getUri();

        //令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";

        //定义header
        LinkedMultiValueMap<String,String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic("XcWebApp", "XcWebApp");
        header.add("Authorization",httpBasic);

        //定义body
        LinkedMultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username","itcast");
        body.add("password","12322");

        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body,header);

        //exchange(String var1, HttpMethod var2, @Nullable HttpEntity<?> var3, Class<T> var4, Object... var5)
        //设置比restTemplate远程调用，对400和401不让报错，正常返回数据
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler(){
            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if(response.getRawStatusCode() != 400 && response.getRawStatusCode() != 401) {
                    super.handleError(response);
                }
            }
        });

        ResponseEntity<Map> exchange = restTemplate.exchange(authUrl, HttpMethod.POST, httpEntity, Map.class);

        //申请的令牌信息
        Map bodyMap = exchange.getBody();
        System.out.println(bodyMap);
    }

    //获取httpBasic的串
    private String getHttpBasic(String clientId,String clientSecret){
        String authorizationHeader = clientId+":"+clientSecret;
        //将httpBasic的串进行Base64编码
        byte[] encode = Base64Utils.encode(authorizationHeader.getBytes());

        return "Basic "+new String(encode);
    }

    @Test
    public void testPasswordEncoder(){
        //原始密码
        String password = "111111";
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        //使用BCrypt加密，每次加密使用一个随机盐
        for(int i=0;i<10;i++){
            String encode = bCryptPasswordEncoder.encode(password);
            System.out.println(encode);
            //校验
            boolean matches = bCryptPasswordEncoder.matches(password, encode);
            System.out.println(matches);
        }
    }

}
