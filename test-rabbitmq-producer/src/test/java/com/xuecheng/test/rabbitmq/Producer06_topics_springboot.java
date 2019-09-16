package com.xuecheng.test.rabbitmq;

import com.xuecheng.test.rabbitmq.config.RabbitmqConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 1:56
 * @Description: RabbitMQ Topics模式
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer06_topics_springboot {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //使用rabbitTemplate发送消息
    @Test
    public void test(){
        String messageEmail = "send topic email inform message to 陈钰琪";
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.email",messageEmail);

        String messageSms = "send topic sms inform message to 陈钰琪";
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.sms",messageSms);

        String messageDirect = "send direct sms inform message to 陈钰琪";
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_DIRECT_INFORM,"inform.email",messageDirect);

        String messageFanout = "send fanout sms inform message to 陈钰琪";
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_FANOUT_INFORM,"inform.email",messageFanout);
    }


}
