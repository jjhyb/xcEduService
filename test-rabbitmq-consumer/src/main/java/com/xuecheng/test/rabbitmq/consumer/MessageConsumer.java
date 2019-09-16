package com.xuecheng.test.rabbitmq.consumer;

import com.rabbitmq.client.Channel;
import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 19:02
 * @Description:
 */

@Component
public class MessageConsumer {

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL_TOPIC})
    public void receiveTopic_email(String msg, Message message, Channel channel){
        System.out.println("receive email topic message is："+msg);
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_SMS_TOPIC})
    public void receiveTopic_sms(String msg, Message message, Channel channel){
        System.out.println("receive sms topic message is："+msg);
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL_FANOUT})
    public void receiveFanout_email(String msg, Message message, Channel channel){
        System.out.println("receive email fanout message is："+msg);
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_SMS_FANOUT})
    public void receiveFanout_sms(String msg, Message message, Channel channel){
        System.out.println("receive sms fanout message is："+msg);
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_EMAIL_DIRECT})
    public void receiveDirect_email(String msg, Message message, Channel channel){
        System.out.println("receive email direct message is："+msg);
    }

    @RabbitListener(queues = {RabbitmqConfig.QUEUE_INFORM_SMS_DIRECT})
    public void receiveDirect_sms(String msg, Message message, Channel channel){
        System.out.println("receive email direct message is："+msg);
    }
}
