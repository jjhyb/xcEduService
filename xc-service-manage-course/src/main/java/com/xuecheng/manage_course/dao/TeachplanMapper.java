package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: huangyibo
 * @Date: 2019/9/6 15:55
 * @Description:
 */

@Mapper
public interface TeachplanMapper {

    //课程计划查询
    public TeachplanNode selectList(String courseId);
}
