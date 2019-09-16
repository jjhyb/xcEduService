package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.CourseBase;
import com.xuecheng.framework.domain.course.CoursePic;
import com.xuecheng.framework.domain.course.Teachplan;
import com.xuecheng.framework.domain.course.TeachplanMedia;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.CoursePublishResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * @author: huangyibo
 * @Date: 2019/9/6 16:15
 * @Description:
 */
public interface CourseService {

    /**
     * 课程计划的查询
     * @param courseId
     * @return
     */
    public TeachplanNode findTeachplanList(String courseId);

    /**
     * 添加课程计划
     * @param teachplan
     * @return
     */
    public ResponseResult addTeachplan(Teachplan teachplan);

    /**
     * 查询用户的课程列表
     * @param page
     * @param size
     * @param courseListRequest
     * @return
     */
    public QueryResponseResult<CourseInfo> findCourseList(int page, int size, CourseListRequest courseListRequest);

    /**
     * 获取课程基础信息
     * @param courseId
     * @return
     * @throws RuntimeException
     */
    public CourseBase getCourseBaseById(String courseId);

    /**
     * 更新课程基础信息
     * @param id
     * @param courseBase
     * @return
     */
    public ResponseResult updateCourseBase(String id,CourseBase courseBase);

    /**
     * 添加课程图片与课程的关联信息
     * @param courseId
     * @param pic
     * @return
     */
    public ResponseResult addCoursePic(String courseId,String pic);

    /**
     * 获取课程图片信息
     * @param courseId
     * @return
     */
    public CoursePic findCoursePic(String courseId);

    /**
     * 删除课程图片
     * @param courseId
     * @return
     */
    public ResponseResult deleteCoursePic(String courseId);

    /**
     * 课程视图查询
     * @param id
     * @return
     */
    public CourseView getCoruseView(String id);

    /**
     * 预览课程
     * @param id
     * @return
     */
    public CoursePublishResult preview(String id);

    /**
     * 课程发布
     * @param id
     * @return
     */
    public CoursePublishResult publish(String id);

    /**
     * 保存课程计划与媒资文件的关联信息
     * @param teachplanMedia
     * @return
     */
    public ResponseResult saveMedia(TeachplanMedia teachplanMedia);
}
