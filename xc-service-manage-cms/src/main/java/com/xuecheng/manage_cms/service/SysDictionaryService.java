package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;

/**
 * @author: huangyibo
 * @Date: 2019/9/6 20:36
 * @Description:
 */
public interface SysDictionaryService {

    public SysDictionary getByType(String type);
}
