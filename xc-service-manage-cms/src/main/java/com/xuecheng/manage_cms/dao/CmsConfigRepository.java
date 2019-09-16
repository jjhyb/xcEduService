package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsConfig;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author: huangyibo
 * @Date: 2019/9/4 18:07
 * @Description:
 */
public interface CmsConfigRepository extends MongoRepository<CmsConfig,String> {
}
