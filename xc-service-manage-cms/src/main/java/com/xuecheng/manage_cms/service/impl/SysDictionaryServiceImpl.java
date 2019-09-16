package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDictionaryRepository;
import com.xuecheng.manage_cms.service.SysDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: huangyibo
 * @Date: 2019/9/6 20:37
 * @Description:
 */

@Service
public class SysDictionaryServiceImpl implements SysDictionaryService {

    @Autowired
    private SysDictionaryRepository sysDictionaryRepository;

    @Override
    public SysDictionary getByType(String type) {

        return sysDictionaryRepository.findByDType(type);
    }
}
