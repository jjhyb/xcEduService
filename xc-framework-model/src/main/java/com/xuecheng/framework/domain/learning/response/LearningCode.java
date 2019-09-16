package com.xuecheng.framework.domain.learning.response;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @author: huangyibo
 * @Date: 2019/9/13 2:08
 * @Description:
 */
public enum LearningCode implements ResultCode {

    LEARNING_GETMEDIA_ERROR(false,23001,"获取视频播放地址出错！"),
    CHOOSECOURSE_USERISNULL(false,23002,"选课信息中userId不能为空！"),
    CHOOSECOURSE_TASKISNULL(false,23002,"选课信息中消息id不能为空！");

    //操作代码
    boolean success;
    //操作代码
    int code;
    //提示信息
    String message;
    private LearningCode(boolean success, int code, String message){
        this.success = success;
        this.code = code;
        this.message = message;
    }

    @Override
    public boolean success() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
