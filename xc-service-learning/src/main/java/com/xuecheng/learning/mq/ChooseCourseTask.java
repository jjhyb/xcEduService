package com.xuecheng.learning.mq;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.learning.config.RabbitMQConfig;
import com.xuecheng.learning.service.LearningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/9/16 2:13
 * @Description:
 */

@Component
public class ChooseCourseTask {

    private static final Logger logger = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    LearningService learningService;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_ADDCHOOSECOURSE})
    public void receiveChoosecourseTask(XcTask xcTask, Message message, Channel channel){
        logger.info("receive choose course task,taskId:{}",xcTask.getId());
        //接收到的消息id
        String id = xcTask.getId();
        try {
            //取到消息的内容
            String requestBody = xcTask.getRequestBody();
            Map map = JSON.parseObject(requestBody, Map.class);
            String userId = (String)map.get("userId");
            String courseId = (String)map.get("courseId");
            String valid = (String) map.get("valid");
            Date startTime = null;
            Date endTime = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY‐MM‐dd HH:mm:ss");
            if(map.get("startTime")!=null){
                startTime =dateFormat.parse((String) map.get("startTime"));
            }
            if(map.get("endTime")!=null){
                endTime =dateFormat.parse((String) map.get("endTime"));
            }
            //添加选课
            ResponseResult responseResult = learningService.addCourse(userId, courseId, valid, startTime, endTime, xcTask);
            //选课成功发送响应消息
            if(responseResult.isSuccess()){
                //发送响应消息
                rabbitTemplate.convertAndSend(RabbitMQConfig.EX_LEARNING_ADDCHOOSECOURSE,
                        RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE_KEY, xcTask);
                logger.info("send finish choose course taskId:{}",id);
            }
        } catch (ParseException e) {
            logger.error("send finish choose course taskId={},e={}", id,e);
        }
    }
}
