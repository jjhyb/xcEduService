package com.xuecheng.auth.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.client.XcServiceList;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.exception.ExceptionCast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.*;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: huangyibo
 * @Date: 2019/9/13 23:52
 * @Description:
 */

@Service
public class AuthService {

    private Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${auth.tokenValiditySeconds}")
    private long tokenValiditySeconds;

    /**
     * 用户认证，申请令牌，并将令牌存储到service
     */
    public AuthToken login(String username, String password, String clientId, String clientSecret){
        //向Spring Security Oauth2请求令牌
        AuthToken authToken = this.applyToken(username, password, clientId, clientSecret);
        if(authToken == null){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_APPLYTOKEN_FAIL);
        }
        //将令牌存储到redis中
        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //存储到redis中的内容
        String content = JSON.toJSONString(authToken);
        boolean saveToken = this.saveToken(access_token, content, tokenValiditySeconds);
        if(!saveToken){
            ExceptionCast.cast(AuthCode.AUTH_LOGIN_TOKEN_SAVEFAIL);
        }
        return authToken;
    }

    /**
     * 从redis中查询令牌
     * @param token
     * @return
     */
    public AuthToken getUserToken(String token){
        String key = "user_token:" + token;
        //从redis中取到的令牌信息
        String value = (String)redisTemplate.boundValueOps(key).get();
        //转成对象
        AuthToken authToken = null;
        try {
            authToken = JSON.parseObject(value, AuthToken.class);
        } catch (Exception e) {
            logger.error("authToken转换出错，e={}",e);
        }
        return authToken;
    }

    /**
     * 删除redis中的token
     * @param access_token 用户身份令牌
     * @return
     */
    public boolean delToken(String access_token){
        //key
        String key = "user_token:" + access_token;
        redisTemplate.delete(key);
        return true;
    }

    /**
     * 存储令牌到redis
     * @param access_token 用户身份令牌
     * @param content authToken内容
     * @param ttl 过期时间
     * @return
     */
    private boolean saveToken(String access_token,String content,long ttl){
        //key
        String key = "user_token:" + access_token;
        redisTemplate.boundValueOps(key).set(content,ttl,TimeUnit.SECONDS);
        Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return expire > 0;
    }


    /**
     * 申请令牌
     * @param username
     * @param password
     * @param clientId
     * @param clientSecret
     * @return
     */
    private AuthToken applyToken(String username, String password, String clientId, String clientSecret){
        //从Eureka中获取认证服务的地址，因为Spring Security Oauth2在认证服务中
        //从Eureka中获取认证服务的一个实例地址
        ServiceInstance serviceInstance = loadBalancerClient.choose(XcServiceList.XC_SERVICE_UCENTER_AUTH);
        //此地址就是http://ip:port
        URI uri = serviceInstance.getUri();

        //令牌申请的地址 http://localhost:40400/auth/oauth/token
        String authUrl = uri + "/auth/oauth/token";

        //定义header
        LinkedMultiValueMap<String,String> header = new LinkedMultiValueMap<>();
        String httpBasic = getHttpBasic(clientId, clientSecret);
        header.add("Authorization",httpBasic);

        //定义body
        LinkedMultiValueMap<String,String> body = new LinkedMultiValueMap<>();
        body.add("grant_type","password");
        body.add("username",username);
        body.add("password",password);

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
        if(CollectionUtils.isEmpty(bodyMap) || StringUtils.isEmpty(bodyMap.get("access_token"))
                || StringUtils.isEmpty(bodyMap.get("refresh_token"))
                || StringUtils.isEmpty(bodyMap.get("jti"))){
            //解析Spring Security Oauth2返回的信息
            if(!StringUtils.isEmpty(bodyMap.get("error_description"))){
                String error_description = (String)bodyMap.get("error_description");
                if(error_description.indexOf("UserDetailsService returned null") >= 0){
                    //证明账号不存在
                    ExceptionCast.cast(AuthCode.AUTH_ACCOUNT_NOTEXISTS);
                }else if(error_description.indexOf("坏的凭证") >= 0){
                    //证明账号或密码错误
                    ExceptionCast.cast(AuthCode.AUTH_CREDENTIAL_ERROR);
                }
            }

            return null;
        }

        AuthToken authToken = new AuthToken();
        //访问令牌(jwt)
        String jwt_token = (String) bodyMap.get("access_token");
        //刷新令牌(jwt)
        String refresh_token = (String) bodyMap.get("refresh_token");
        //jti，作为用户的身份标识
        String access_token = (String) bodyMap.get("jti");
        authToken.setJwt_token(jwt_token);
        authToken.setAccess_token(access_token);
        authToken.setRefresh_token(refresh_token);
        return authToken;
    }

    //获取httpBasic的串
    private String getHttpBasic(String clientId,String clientSecret){
        String authorizationHeader = clientId+":"+clientSecret;
        //将httpBasic的串进行Base64编码
        byte[] encode = Base64Utils.encode(authorizationHeader.getBytes());

        return "Basic "+new String(encode);
    }


}
