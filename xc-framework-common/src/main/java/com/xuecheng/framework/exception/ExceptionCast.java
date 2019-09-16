package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @author: huangyibo
 * @Date: 2019/9/3 20:57
 * @Description:
 */
public class ExceptionCast {

    public static void  cast(ResultCode resultCode){
        throw new CustomException(resultCode);
    }
}
