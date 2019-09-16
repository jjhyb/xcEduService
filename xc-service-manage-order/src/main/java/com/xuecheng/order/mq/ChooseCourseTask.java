package com.xuecheng.order.mq;

import com.rabbitmq.client.Channel;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/9/15 23:15
 * @Description:
 */

@Component
public class ChooseCourseTask {

    private static final Logger logger = LoggerFactory.getLogger(ChooseCourseTask.class);

    @Autowired
    private TaskService taskService;

    /**
     * 接收选课响应结果
     */
    @RabbitListener(queues = {RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE})
    public void receiveFinishChoosecourseTask(XcTask xcTask, Message message, Channel channel){
        logger.info("receiveChoosecourseTask xcTask={}",xcTask);
        if(xcTask != null && !StringUtils.isEmpty(xcTask.getId())){
            //删除任务，添加历史任务
            taskService.finishTask(xcTask.getId());
        }

    }


    //定时发送添加选课任务
    @Scheduled(cron="0/3 * * * * *")//每隔3秒执行一次
    public void sendChooseCourseTask(){
        //得到1分钟之前的时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE,-1);
        //得到1分钟之前的时间
        Date time = calendar.getTime();
        List<XcTask> taskList = taskService.findTaskList(time, 1000);

        //调用service发布消息，将添加选课的消息发送给rabbitmq
        if(!CollectionUtils.isEmpty(taskList)){
            for (XcTask xcTask : taskList) {
                //取任务
                if(taskService.getTask(xcTask.getId(),xcTask.getVersion()) > 0){
                    //消息发送的交换机
                    String exchange = xcTask.getMqExchange();
                    //消息路由的routingkey
                    String routingKey = xcTask.getMqRoutingkey();
                    taskService.publish(xcTask,exchange,routingKey);
                }
            }
        }
    }


    //定义任务调度策略
//    @Scheduled(cron="0/3 * * * * *")//每隔3秒执行一次
//    @Scheduled(fixedRate = 3000) //在任务开始后3秒执行下一次调度
//    @Scheduled(fixedDelay = 3000) //在上次任务执行完毕后3秒开始执行
//    @Scheduled(initialDelay=3000, fixedRate=5000) //第一次延迟3秒，以后每隔5秒执行一次
    public void test1(){
        logger.info("===============测试定时任务1开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("===============测试定时任务1结束===============");
    }

    //定义任务调度策略
//    @Scheduled(cron="0/3 * * * * *")//每隔3秒执行一次
//    @Scheduled(fixedRate = 3000) //在任务开始后3秒执行下一次调度
//    @Scheduled(fixedDelay = 3000) //在上次任务执行完毕后3秒开始执行
//    @Scheduled(initialDelay=3000, fixedRate=5000) //第一次延迟3秒，以后每隔5秒执行一次
    public void test2(){
        logger.info("===============测试定时任务2开始===============");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("===============测试定时任务2结束===============");
    }
}
