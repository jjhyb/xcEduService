package com.xuecheng.govern.gateway.service;

import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: huangyibo
 * @Date: 2019/9/14 20:09
 * @Description:
 */

@Service
public class AuthService {

    private static final String headerPre = "Bearer ";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 从header头中取出jwt令牌
     * @param request
     * @return
     */
    public String getJwtFromHeader(HttpServletRequest request){
        //得到Authorization
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            return null;
        }
        if(authorization.startsWith(headerPre)){
            return null;
        }
        //取到jwt令牌
        String jwtToken = authorization.substring(headerPre.length());
        return jwtToken;
    }

    //从cookie中取出用户身份token
    public String getTokenFromCookie(HttpServletRequest request){
        Map<String, String> cookieMap = CookieUtil.readCookie(request, "access_token");
        String access_token = cookieMap.get("access_token");
        if(StringUtils.isEmpty(access_token)){
            return null;
        }
        return access_token;
    }


    //查询redis中的jwt令牌是否过期
    public long getExpire(String access_token) {
        //key
        String key = "user_token:" + access_token;
        Long expire = redisTemplate.getExpire(key,TimeUnit.SECONDS);
        return expire;
    }
}
