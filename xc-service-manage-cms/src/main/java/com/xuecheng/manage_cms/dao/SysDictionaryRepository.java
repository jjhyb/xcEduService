package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author: huangyibo
 * @Date: 2019/9/2 19:42
 * @Description:
 */

public interface SysDictionaryRepository extends MongoRepository<SysDictionary,String> {


    //根据页面名称、站点ID、页面webPath查询
    SysDictionary findByDType(String type);
}
