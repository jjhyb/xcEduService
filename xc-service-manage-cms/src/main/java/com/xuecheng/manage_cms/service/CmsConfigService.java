package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsConfig;

/**
 * @author: huangyibo
 * @Date: 2019/9/4 18:09
 * @Description:
 */
public interface CmsConfigService {

    /**
     * 根据id查询CmsConfig
     * @param id
     * @return
     */
    public CmsConfig getConfigById(String id);
}
