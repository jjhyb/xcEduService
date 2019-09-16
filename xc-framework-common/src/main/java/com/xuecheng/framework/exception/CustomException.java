package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @author: huangyibo
 * @Date: 2019/9/3 20:54
 * @Description:  自定义异常类型
 *
 */
public class CustomException extends RuntimeException {

    //错误代码
    private ResultCode resultCode;

    public CustomException(ResultCode resultCode){
        this.resultCode = resultCode;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
