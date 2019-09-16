package com.xuecheng.framework.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author: huangyibo
 * @Date: 2019/9/15 18:12
 * @Description: Feign 拦截器 在哪个服务使用就在哪个服务定义Bean
 */
public class FeignClientInterceptor implements RequestInterceptor {

    /**
     * 每次Feign发起远程调用，都会调用这个方法
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes servletRequestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if(servletRequestAttributes != null){
            HttpServletRequest request = servletRequestAttributes.getRequest();
            //取出当前请求的header，找到jwt令牌
            Enumeration<String> headerNames = request.getHeaderNames();
            if(headerNames != null){
                while (headerNames.hasMoreElements()){
                    String headerName = headerNames.nextElement();
                    String headerValue = request.getHeader(headerName);
                    //将jwt令牌向下传递
                    requestTemplate.header(headerName,headerValue);
                }
            }
        }



    }
}
