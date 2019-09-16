package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;

/**
 * @author: huangyibo
 * @Date: 2019/9/2 21:00
 * @Description:
 */
public interface PageService {

    /**
     * 页面查询方法
     * @param page 页码，从1开始记数
     * @param size 每页记录数
     * @param queryPageRequest 查询条件
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest);

    /**
     * 新增页面
     * @param cmsPage
     * @return
     */
    public CmsPageResult add(CmsPage cmsPage);

    /**
     * 通过ID查询页面
     * @param id
     * @return
     */
    public CmsPage findById(String id);

    /**
     * 修改页面
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPageResult edit(String id,CmsPage cmsPage);

    /**
     * 通过ID删除页面
     * @param id
     * @return
     */
    public ResponseResult delete(String id);

    /**
     * 页面静态化方法
     * @param pageId
     * @return
     */
    public String getPageHtml(String pageId);

    /**
     * 页面发布
     * @param pageId
     * @return
     */
    public ResponseResult post(String pageId);

    /**
     * 保存页面，有则更新，没有则添加
     * @param cmsPage
     * @return
     */
    public CmsPageResult save(CmsPage cmsPage);

    /**
     * 一键发布页面
     * @param cmsPage
     * @return
     */
    public CmsPostPageResult postPageQuick(CmsPage cmsPage);
}
