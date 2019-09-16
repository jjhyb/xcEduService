package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author: huangyibo
 * @Date: 2019/9/4 18:07
 * @Description:
 */

public interface FileSystemRepository extends MongoRepository<FileSystem,String> {
}
