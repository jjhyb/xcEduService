package com.xuecheng.test.rabbitmq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 18:30
 * @Description:
 */

@Configuration
public class RabbitmqConfig {

    @Autowired
    private CachingConnectionFactory connectionFactory;

    //队列
    public static final String QUEUE_INFORM_EMAIL_TOPIC = "queue_inform_email_topic";
    public static final String QUEUE_INFORM_SMS_TOPIC = "queue_inform_sms_topic";

    public static final String QUEUE_INFORM_EMAIL_FANOUT = "queue_inform_email_fanout";
    public static final String QUEUE_INFORM_SMS_FANOUT = "queue_inform_sms_fanout";

    public static final String QUEUE_INFORM_EMAIL_DIRECT = "queue_inform_email_direct";
    public static final String QUEUE_INFORM_SMS_DIRECT = "queue_inform_sms_direct";

    //Routingkey
    public static final String TOPIC_ROUTINGKEY_EMAIL = "inform.#.email.#";//inform.email、inform.email.sms都可以匹配
    public static final String TOPIC_ROUTINGKEY_SMS = "inform.#.sms.#";//inform.sms、inform.email.sms都可以匹配

    private static final String DIRECT_ROUTINGKEY_EMAIL = "inform_email";
    private static final String DIRECT_ROUTINGKEY_SMS = "inform_sms";

    //交换机
    public static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";
    public static final String EXCHANGE_DIRECT_INFORM="exchange_direct_inform";
    public static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";


    //===============以下是验证Direct Exchange的队列==========
    @Bean(QUEUE_INFORM_EMAIL_DIRECT)
    public Queue userQueue() {
        return new Queue(QUEUE_INFORM_EMAIL_DIRECT);
    }

    @Bean(QUEUE_INFORM_SMS_DIRECT)
    public Queue dirQueue() {
        return new Queue(QUEUE_INFORM_SMS_DIRECT);
    }
    //===============以上是验证Direct Exchange的队列==========

    //===============以下是验证topic Exchange的队列==========
    //声明队列
    @Bean(QUEUE_INFORM_EMAIL_TOPIC)
    public Queue emailTopicQueue(){
        return new Queue(QUEUE_INFORM_EMAIL_TOPIC);
    }

    @Bean(QUEUE_INFORM_SMS_TOPIC)
    public Queue smsTopicQueue(){
        return new Queue(QUEUE_INFORM_SMS_TOPIC);
    }

    //===============以上是验证topic Exchange的队列==========

    //===============以下是验证Fanout Exchange的队列==========

    @Bean(QUEUE_INFORM_EMAIL_FANOUT)
    public Queue emailFanoutQueue(){
        return new Queue(QUEUE_INFORM_EMAIL_FANOUT);
    }

    @Bean(QUEUE_INFORM_SMS_FANOUT)
    public Queue smsFanoutQueue(){
        return new Queue(QUEUE_INFORM_SMS_FANOUT);
    }

    //===============以上是验证Fanout Exchange的队列==========
    /**
     *   exchange是交换机交换机的主要作用是接收相应的消息并且绑定到指定的队列.交换机有四种类型,分别为Direct,topic,headers,Fanout.
     *
     * 　Direct是RabbitMQ默认的交换机模式,也是最简单的模式.即创建消息队列的时候,指定一个BindingKey.当发送者发送消息的时候,指定对应的Key.当Key和消息队列的BindingKey一致的时候,消息将会被发送到该消息队列中.
     *
     * 　topic转发信息主要是依据通配符,队列和交换机的绑定主要是依据一种模式(通配符+字符串),而当发送消息的时候,只有指定的Key和该模式相匹配的时候,消息才会被发送到该消息队列中.
     *
     * 　headers也是根据一个规则进行匹配,在消息队列和交换机绑定的时候会指定一组键值对规则,而发送消息的时候也会指定一组键值对规则,当两组键值对规则相匹配的时候,消息会被发送到匹配的消息队列中.
     *
     * 　Fanout是路由广播的形式,将会把消息发给绑定它的全部队列,即便设置了key,也会被忽略.
     */

    //声明交换机
    @Bean(EXCHANGE_TOPICS_INFORM)
    public Exchange topicExchange(){
        //durable(true)：持久化，mq重启之后交换机还在
        ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
        return ExchangeBuilder.topicExchange(EXCHANGE_TOPICS_INFORM).durable(true).build();
    }

    @Bean(EXCHANGE_DIRECT_INFORM)
    public Exchange directExchange(){
        //durable(true)：持久化，mq重启之后交换机还在
        return ExchangeBuilder.directExchange(EXCHANGE_DIRECT_INFORM).durable(true).build();
    }

    //配置广播交换器
    @Bean(EXCHANGE_FANOUT_INFORM)
    public Exchange fanoutExchange(){
        //durable(true)：持久化，mq重启之后交换机还在
        return ExchangeBuilder.fanoutExchange(EXCHANGE_FANOUT_INFORM).durable(true).build();
    }

    //绑定QUEUE_INFORM_EMAIL_TOPIC队列和交换机，并且指定routingkey
    @Bean
    public Binding bindTopicEmailQueue(@Qualifier(QUEUE_INFORM_EMAIL_TOPIC) Queue queue,@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(TOPIC_ROUTINGKEY_EMAIL).noargs();
    }

    //绑定QUEUE_INFORM_SMS_TOPIC队列和交换机，并且指定routingkey
    @Bean
    public Binding bindTopicSmsQueue(@Qualifier(QUEUE_INFORM_SMS_TOPIC) Queue queue,@Qualifier(EXCHANGE_TOPICS_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(TOPIC_ROUTINGKEY_SMS).noargs();
    }

    //绑定QUEUE_INFORM_EMAIL_FANOUT队列和交换机，并且指定routingkey
    @Bean
    public Binding bindFanoutEmailQueue(@Qualifier(QUEUE_INFORM_EMAIL_FANOUT) Queue queue,@Qualifier(EXCHANGE_FANOUT_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    //绑定QUEUE_INFORM_SMS_FANOUT队列和交换机，并且指定routingkey
    @Bean
    public Binding bindFanoutSmsQueue(@Qualifier(QUEUE_INFORM_SMS_FANOUT) Queue queue,@Qualifier(EXCHANGE_FANOUT_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with("").noargs();
    }

    //绑定QUEUE_INFORM_EMAIL_DIRECT队列和交换机，并且指定routingkey
    @Bean
    public Binding bindDirectEmailQueue(@Qualifier(QUEUE_INFORM_EMAIL_DIRECT) Queue queue,@Qualifier(EXCHANGE_DIRECT_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(DIRECT_ROUTINGKEY_EMAIL).noargs();
    }

    //绑定QUEUE_INFORM_SMS_DIRECT队列和交换机，并且指定routingkey
    @Bean
    public Binding bindDirectSmsQueue(@Qualifier(QUEUE_INFORM_SMS_DIRECT) Queue queue,@Qualifier(EXCHANGE_DIRECT_INFORM) Exchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(DIRECT_ROUTINGKEY_SMS).noargs();
    }

}


