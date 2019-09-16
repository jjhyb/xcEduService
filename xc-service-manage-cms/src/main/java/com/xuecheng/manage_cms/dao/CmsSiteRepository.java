package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author: huangyibo
 * @Date: 2019/9/8 22:10
 * @Description:
 */
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
