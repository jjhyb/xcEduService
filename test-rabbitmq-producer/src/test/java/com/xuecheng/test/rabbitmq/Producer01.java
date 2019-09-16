package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 1:56
 * @Description: RabbitMQ Work queues模式
 */
public class Producer01 {

    //队列
    public static final String QUEUE = "helloworld";

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

            //声明队列，使用默认的交换机
            /**
             * 参数：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
             * 参数明细：
             * String queue ：队列名称
             * boolean durable ：是否持久化消息，持久化的话，mq服务宕机重启后未消费的消息还在
             * boolean exclusive ：是否独占连接，队列只允许在该连接中访问，如果连接关闭队列自动删除，如果将此参数设置为true可用于临时队列的创建
             * boolean autoDelete ：自动删除，队列不再使用时是否自动删除此队列，如果将此参数和exclusive参数都设置为true就可以实现临时队列(队列不用了就自动删除)
             * Map<String, Object> arguments ：可以设置一个队列的扩展参数，比如存活时间
             */
            channel.queueDeclare(QUEUE,true,false,false,null);

            //发送消息
            /**
             * String exchange, String routingKey, BasicProperties props, byte[] body
             * 参数明细：
             * String exchange ：交换机，如果不指定将使用mq默认的交换机(设置为"")
             * String routingKey ：路由key，交换机根据路由key将消息转发到指定队列，如果使用默认交换机，routingKey要设置为队列的名称
             * BasicProperties props ：扩展的消息的属性
             * byte[] body ：消息内容
             */
            String message = "hello world 陈钰琪";
            channel.basicPublish("",QUEUE,null,message.getBytes());
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
