package com.xuecheng.manage_cms_client.consumer;

import com.alibaba.fastjson.JSON;
import com.xuecheng.manage_cms_client.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 23:28
 * @Description: 监听MQ，接收页面发布的消息
 */

@Component
public class ConsumerPostPage {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerPostPage.class);

    @Autowired
    private PageService pageService;

    @RabbitListener(queues = {"${xuecheng.mq.queue}"})
    public void postPage(String message){
        //解析消息
        Map map = JSON.parseObject(message, Map.class);
        //得到消息中页面的id
        String pageId = (String)map.get("pageId");
        //校验页面是否合法 ----> 这一步在后面已经校验了
        //调用service方法将页面从GridFS中下载到服务器
        pageService.savePageToServerPath(pageId);
    }
}
