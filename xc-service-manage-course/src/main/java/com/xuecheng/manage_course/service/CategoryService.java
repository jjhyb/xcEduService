package com.xuecheng.manage_course.service;

import com.xuecheng.framework.domain.course.ext.CategoryNode;

/**
 * @author: huangyibo
 * @Date: 2019/9/6 20:18
 * @Description:
 */
public interface CategoryService {

    /**
     * 课程分类查询
     * @return
     */
    public CategoryNode findList();
}
