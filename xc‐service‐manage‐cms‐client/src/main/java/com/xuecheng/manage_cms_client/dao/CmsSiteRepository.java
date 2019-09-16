package com.xuecheng.manage_cms_client.dao;

import com.xuecheng.framework.domain.cms.CmsSite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * @author: huangyibo
 * @Date: 2019/9/2 19:42
 * @Description:
 */
@Component
public interface CmsSiteRepository extends MongoRepository<CmsSite,String> {
}
