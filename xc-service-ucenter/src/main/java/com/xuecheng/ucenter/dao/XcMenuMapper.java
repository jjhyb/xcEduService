package com.xuecheng.ucenter.dao;

import com.xuecheng.framework.domain.ucenter.XcMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/9/15 0:18
 * @Description:
 */

@Mapper
public interface XcMenuMapper {

    //根据用户id查询用户权限
    public List<XcMenu> selectPermissionByUserId(@Param("userId")String userId);

}
