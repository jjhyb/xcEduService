package com.xuecheng.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 2:30
 * @Description: RabbitMQ Topics模式
 */
public class Consumer04_topics_email {

    //队列
    private static final String QUEUE_INFORM_EMAIL = "queue_inform_email";

    //Routingkey
    private static final String ROUTINGKEY_EMAIL = "inform.#.email.#";//inform.email、inform.email.sms都可以匹配

    //交换机
    private static final String EXCHANGE_TOPICS_INFORM="exchange_topics_inform";

    public static void main(String[] args) throws IOException, TimeoutException {
        //通过连接工厂创建新的连接和mq建立连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        //设置虚拟机，一个mq的服务可以设置多个虚拟机，每个虚拟机就相当于一个独立的mq
        connectionFactory.setVirtualHost("/");

        Connection connection = null;

        //建立连接
        connection = connectionFactory.newConnection();
        //创建会话通道，生产者和mq服务所有的通信都在channel通道中完成
        Channel channel = connection.createChannel();

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
        channel.exchangeDeclare(EXCHANGE_TOPICS_INFORM, BuiltinExchangeType.TOPIC);

        //将交换机和队列进行绑定
        /**
         * String queue, String exchange, String routingKey
         * 参数明细：
         * String queue：队列名称
         * String exchange：交换机名称
         * String routingKey：路由key，作用为交换机会根据路由key将消息转发到对应的队列中，在发布订阅模式中设置为空字符串
         */
        channel.queueBind(QUEUE_INFORM_EMAIL,EXCHANGE_TOPICS_INFORM,ROUTINGKEY_EMAIL);

        //实现消费方法
        DefaultConsumer consumer = new DefaultConsumer(channel){
            /**
             * 当监听到消息后，此方法会被调用
             * @param consumerTag 消费者标签，用来标识消费者，在监听队列是设置channel.basicConsume
             * @param envelope 信封，通过envelope
             * @param properties 消息属性
             * @param body 消息内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                //交换机
                String exchange = envelope.getExchange();
                //消息id，mq在channel中用来标识消息的id，可用于确认消息已接收
                long deliveryTag = envelope.getDeliveryTag();
                //消息内容
                String message = new String(body, "UTF-8");
                System.out.println("receive message："+message);
            }
        };

        //监听队列
        /**
         * String queue, boolean autoAck, Consumer callback
         * 参数明细：
         * String queue ：队列名称
         * boolean autoAck ：自动回复，当消费者接收到消息后要告诉mq已消费，true表示自动回复，false需要通过编程回复
         * Consumer callback ：消费方法，当消费者监听到消息，需要执行的回调方法
         *
         */
        channel.basicConsume(QUEUE_INFORM_EMAIL,true,consumer);
    }
}
