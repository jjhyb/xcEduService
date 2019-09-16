package com.xuecheng.order.service;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author: huangyibo
 * @Date: 2019/9/16 0:14
 * @Description:
 */

@Service
public class TaskService {

    @Autowired
    private XcTaskRepository xcTaskRepository;

    @Autowired
    private XcTaskHisRepository xcTaskHisRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //取出前n条任务,取出指定时间之前处理的任务
    public List<XcTask> findTaskList(Date updateTime, int size) {
        //我个人觉得这里有问题，这里应该是查询从现在开始已经在XcTask表中没有处理的消息
        //设置分页参数，
        Pageable pageable = PageRequest.of(0, size);
        Page<XcTask> page = xcTaskRepository.findByUpdateTimeBefore(pageable, updateTime);
        return page.getContent();
    }

    //发布消息
    @Transactional
    public void publish(XcTask xcTask, String exchange, String routingKey) {
        Optional<XcTask> optional = xcTaskRepository.findById(xcTask.getId());
        if (optional.isPresent()) {
            rabbitTemplate.convertAndSend(exchange, routingKey, xcTask);
            //更新任务的时间
            XcTask one = optional.get();
            one.setUpdateTime(new Date());
            xcTaskRepository.save(one);
        }
    }

    //获取任务
    @Transactional
    public int getTask(String id, int version) {
        //通过乐观锁的方式，更新数据库表如果结果大于0，说明取到任务
        int count = xcTaskRepository.updateTaskVersion(id, version);
        return count;
    }

    //完成任务
    @Transactional
    public void finishTask(String taskId){
        Optional<XcTask> taskOptional = xcTaskRepository.findById(taskId);
        if(taskOptional.isPresent()){
            //当前任务
            XcTask xcTask = taskOptional.get();
            xcTask.setDeleteTime(new Date());
            //历史任务
            XcTaskHis xcTaskHis = new XcTaskHis();
            BeanUtils.copyProperties(xcTask, xcTaskHis);
            xcTaskHisRepository.save(xcTaskHis);
            xcTaskRepository.delete(xcTask);
        }
    }

}
