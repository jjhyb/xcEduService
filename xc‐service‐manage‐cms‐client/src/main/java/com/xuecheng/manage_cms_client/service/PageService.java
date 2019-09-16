package com.xuecheng.manage_cms_client.service;

import com.xuecheng.framework.domain.cms.CmsPage;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 22:37
 * @Description:
 */
public interface PageService {

    /**
     * 保存html页面到服务器的物理路径
     * @param pageId
     */
    public void savePageToServerPath(String pageId);

    /**
     * 根据页面id查询页面的html文件信息
     * @param pageId
     * @return
     */
    public CmsPage findCmsPageById(String pageId);
}
