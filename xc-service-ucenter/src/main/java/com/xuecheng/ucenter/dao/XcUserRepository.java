package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: huangyibo
 * @Date: 2019/9/14 13:28
 * @Description:
 */
public interface XcUserRepository extends JpaRepository<XcUser,String> {

    //根据账户查询用户信息
    XcUser findByUsername(String username);
}
