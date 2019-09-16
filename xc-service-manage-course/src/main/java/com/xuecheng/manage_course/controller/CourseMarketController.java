package com.xuecheng.manage_course.controller;

import com.xuecheng.api.course.CourseMarketControllerApi;
import com.xuecheng.framework.domain.course.CourseMarket;
import com.xuecheng.framework.model.response.ResponseResult;
import org.springframework.web.bind.annotation.*;

/**
 * @author: huangyibo
 * @Date: 2019/9/6 22:44
 * @Description:
 */

@RestController
@RequestMapping("/course/market")
public class CourseMarketController implements CourseMarketControllerApi {

    @Override
    @GetMapping("/find/{courseId}")
    public CourseMarket getCourseMarketById(@PathVariable("courseId") String courseId) {
        return null;
    }

    @Override
    @PostMapping("/update/{id}")
    public ResponseResult updateCourseMarket(@PathVariable("id") String id, @RequestBody CourseMarket courseMarket) {
        return null;
    }
}
