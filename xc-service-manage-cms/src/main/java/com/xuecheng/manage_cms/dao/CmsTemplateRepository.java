package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author: huangyibo
 * @Date: 2019/9/4 22:52
 * @Description:
 */
public interface CmsTemplateRepository extends MongoRepository<CmsTemplate,String> {
}
