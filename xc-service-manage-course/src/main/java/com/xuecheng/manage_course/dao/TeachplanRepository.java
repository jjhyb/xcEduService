package com.xuecheng.manage_course.dao;

import com.xuecheng.framework.domain.course.Teachplan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created by Administrator.
 */
public interface TeachplanRepository extends JpaRepository<Teachplan,String> {

    //根据课程id和parentId查询Teachplan SELECT * FROM teachplan WHERE courseid = '4028e581617f945f01617f9dabc40000' AND parentid = '0';
    public List<Teachplan> findByCourseidAndParentid(String courseId,String parentId);
}
