package com.xuecheng.auth.controller;

import com.xuecheng.api.auth.AuthControllerApi;
import com.xuecheng.auth.service.AuthService;
import com.xuecheng.framework.domain.ucenter.ext.AuthToken;
import com.xuecheng.framework.domain.ucenter.request.LoginRequest;
import com.xuecheng.framework.domain.ucenter.response.AuthCode;
import com.xuecheng.framework.domain.ucenter.response.JwtResult;
import com.xuecheng.framework.domain.ucenter.response.LoginResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/9/13 23:51
 * @Description:
 */

@RestController
@RequestMapping("/")
public class AuthController implements AuthControllerApi {

    @Value("${auth.clientId}")
    private String clientId;

    @Value("${auth.clientSecret}")
    private String clientSecret;

    @Value("${auth.cookieDomain}")
    private String cookieDomain;

    @Value("${auth.cookieMaxAge}")
    private int cookieMaxAge;

    @Autowired
    private AuthService authService;

    @Override
    @PostMapping("/userlogin")
    public LoginResult login(LoginRequest loginRequest) {
        if(loginRequest == null || StringUtils.isEmpty(loginRequest.getUsername())){
            ExceptionCast.cast(AuthCode.AUTH_USERNAME_NONE);
        }

        if(StringUtils.isEmpty(loginRequest.getPassword())){
            ExceptionCast.cast(AuthCode.AUTH_PASSWORD_NONE);
        }
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        //申请令牌
        AuthToken authToken = authService.login(username, password, clientId, clientSecret);

        //用户身份令牌
        String access_token = authToken.getAccess_token();
        //将令牌token存到cookie
        this.saveCookie(access_token);
        return new LoginResult(CommonCode.SUCCESS,access_token);
    }

    //将令牌token存到cookie
    private void saveCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        //addCookie(HttpServletResponse response,String domain,String path, String name,String value, int maxAge,boolean httpOnly)

        CookieUtil.addCookie(response,cookieDomain,"/","access_token",token,cookieMaxAge,false);
    }

    @Override
    @PostMapping("/userlogout")
    public ResponseResult logout() {
        //取出cookie中的用户身份令牌
        String access_token = this.getTokenFormCookie();
        //删除redis中的token
        authService.delToken(access_token);

        //将cookie中的token删除
        this.clearCookie(access_token);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 将cookie中的token删除
     * @param token
     */
    private void clearCookie(String token){
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

        //addCookie(HttpServletResponse response,String domain,String path, String name,String value, int maxAge,boolean httpOnly)

        CookieUtil.addCookie(response,cookieDomain,"/","access_token",token,0,false);
    }

    /**
     * 查询用户的jwt令牌
     * @return
     */
    @Override
    @GetMapping("/userjwt")
    public JwtResult userJwt() {
        //取出cookie中的用户身份令牌
        String access_token = this.getTokenFormCookie();
        if(StringUtils.isEmpty(access_token)){
            return new JwtResult(CommonCode.FAIL,null);
        }

        //拿用户身份令牌从redis中查询jwt令牌
        AuthToken authToken = authService.getUserToken(access_token);
        if(authToken != null){
            String jwt_token = authToken.getJwt_token();
            //将jwt令牌返回给用户
            return new JwtResult(CommonCode.SUCCESS,jwt_token);
        }
        return null;
    }

    /**
     * 取出cookie中的用户身份令牌
     * @return
     */
    private String getTokenFormCookie(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        Map<String, String> map = CookieUtil.readCookie(request, "access_token");
        if(!CollectionUtils.isEmpty(map)){
            return map.get("access_token");
        }
        return null;
    }
}
