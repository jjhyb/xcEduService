package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 1:56
 * @Description: RabbitMQ Publish/Subscribe模式
 */
public class Producer02_publish {

    //队列
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";
    private static final String QUEUE_INFORM_SMS = "queue_inform_sms";

    //交换机
    private static final String EXCHANGE_FANOUT_INFORM="exchange_fanout_inform";

    public static void main(String[] args){
        //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机，一个mq的服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");


        Connection connection = null;
        Channel channel = null;
        try {
            //建立连接
            connection = connectionFactory.newConnection();
            //创建会话通道，生产者和mq服务所有的通信都在channel通道中完成
            channel = connection.createChannel();

            //声明队列
            /**
             * 参数：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
             * 参数明细：
             * String queue ：队列名称
             * boolean durable ：是否持久化消息，持久化的话，mq服务宕机重启后未消费的消息还在
             * boolean exclusive ：是否独占连接，队列只允许在该连接中访问，如果连接关闭队列自动删除，如果将此参数设置为true可用于临时队列的创建
             * boolean autoDelete ：自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数都设置为true就可以实现临时队列(队列不用了就自动删除)
             * Map<String, Object> arguments ：可以设置一个队列的扩展参数，比如存活时间
             */
            channel.queueDeclare(QUEUE_INFORM_EMAIL,true,false,false,null);
            channel.queueDeclare(QUEUE_INFORM_SMS,true,false,false,null);

            //声明一个交换机
            /**
             * String exchange, String type
             * 参数明细：
             * String exchange ：交换机名称
             * String type ：交换机类型，fanout、topic、direct、headers
             * fanout：对应的rabbitmq工作模式是Publish/Subscribe(发布订阅)模式
             * topic：对应的rabbitmq工作模式是Topics模式
             * direct：对应的rabbitmq工作模式是Routing模式
             * headers：对应的rabbitmq工作模式是Header模式
             */
            channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);

            //将交换机和队列进行绑定
            /**
             * String queue, String exchange, String routingKey
             * 参数明细：
             * String queue：队列名称
             * String exchange：交换机名称
             * String routingKey：路由key，作用为交换机会根据路由key将消息转发到对应的队列中，在发布订阅模式中设置为空字符串
             */
            channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_FANOUT_INFORM,"");
            channel.queueBind(QUEUE_INFORM_SMS,EXCHANGE_FANOUT_INFORM,"");

            //发送消息
            /**
             * String exchange, String routingKey, BasicProperties props, byte[] body
             * 参数明细：
             * String exchange ：交换机，如果不指定将使用mq默认的交换机(设置为"")
             * String routingKey ：路由key，交换机根据路由key将消息转发到指定队列，如果使用默认交换机，routingKey要设置为队列的名称
             * BasicProperties props ：扩展的消息的属性
             * byte[] body ：消息内容
             */
            String message = "send inform message to 陈钰琪";
            channel.basicPublish(EXCHANGE_FANOUT_INFORM,"",null,message.getBytes());
            System.out.println("send to mq："+message);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //先关闭通道
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            //后关闭连接
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
