package com.xuecheng.framework.exception;

import com.google.common.collect.ImmutableMap;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: huangyibo
 * @Date: 2019/9/3 21:04
 * @Description: 统一异常捕获类
 */

@ControllerAdvice//控制器增强
public class ExceptionCatch {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionCatch.class);

    //定义Map，配置异常类型所对应的错误代码
    private static ImmutableMap<Class<? extends Throwable>,ResultCode> EXCEPTIONS;
    //定义Map的bulider对象，去构建ImmutableMap
    protected static ImmutableMap.Builder<Class<? extends Throwable>,ResultCode> builder = ImmutableMap.builder();

    static{
        //在这里加入一些基础的异常类型判断
        builder.put(HttpMessageNotReadableException.class,CommonCode.INVALID_PARAM);
    }

    //捕获 CustomException异常
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult customException(CustomException e) {
        LOGGER.error("catch exception : e={}",e);
        ResultCode resultCode = e.getResultCode();
        return new ResponseResult(resultCode);
    }

    //捕获 Exception异常
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception e) {
        LOGGER.error("catch exception : e={}", e);
        if(CollectionUtils.isEmpty(EXCEPTIONS)){
            EXCEPTIONS = builder.build();//上面的ImmutableMap构建成功
        }
        //从EXCEPTIONS中寻找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果没找到给用户响应默认的SERVER_ERROR
        ResultCode resultCode = EXCEPTIONS.get(e.getClass());
        if(resultCode != null){
            return new ResponseResult(resultCode);
        }
        //返回SERVER_ERROR异常
        return new ResponseResult(CommonCode.SERVER_ERROR);
    }

}
