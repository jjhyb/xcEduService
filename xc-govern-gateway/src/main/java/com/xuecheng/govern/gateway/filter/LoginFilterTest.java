package com.xuecheng.govern.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: huangyibo
 * @Date: 2019/9/14 19:38
 * @Description:
 */

//@Component
public class LoginFilterTest extends ZuulFilter {

    /**
     * 设置过滤器的类型
     * filterType：返回字符串代表过滤器的类型，如下
     * pre：请求在被路由之前执行
     * routing：在路由请求时调用
     * post：在routing和errror过滤器之后调用
     * error：处理请求时发生错误调用
     * @return
     */
    @Override
    public String filterType() {
        //这里为用户请求在到达微服务之前校验用户身份，所以设置pre
        return "pre";
    }

    /**
     * 过滤器的序号，越小越被优先执行
     * @return
     */
    @Override
    public int filterOrder() {
        return 0;
    }

    /**
     * shouldFilter：返回一个Boolean值，判断该过滤器是否需要执行。返回true表示要执行此过虑器，否则不执行
     * @return
     */
    @Override
    public boolean shouldFilter() {
        return true;
    }

    /**
     * run：过滤器的业务逻辑
     * 测试需求：过虑所有请求，判断头部信息是否有Authorization，如果没有则拒绝访问，否则转发到微服务
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        //得到request
        HttpServletRequest request = requestContext.getRequest();
        //得到response
        HttpServletResponse response = requestContext.getResponse();
        //得到Authorization
        String authorization = request.getHeader("Authorization");
        if(StringUtils.isEmpty(authorization)){
            ///拒绝访问，不在向下路由转发
            requestContext.setSendZuulResponse(false);
            //设置响应的代码
            requestContext.setResponseStatusCode(200);
            //构建响应的信息
            ResponseResult responseResult = new ResponseResult(CommonCode.UNAUTHENTICATED);
            //转成json
            String responseJson = JSON.toJSONString(responseResult);
            requestContext.setResponseBody(responseJson);
            //设置contentType
            response.setContentType("application/json;charset=UTF-8");
            return null;
        }
        return null;
    }
}
