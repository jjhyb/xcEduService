package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author: huangyibo
 * @Date: 2019/9/14 13:28
 * @Description:
 */
public interface XcCompanyUserRepository extends JpaRepository<XcCompanyUser,String> {

    //根据用户id查询用户所属的公司Id
    XcCompanyUser findByUserId(String userId);
}
