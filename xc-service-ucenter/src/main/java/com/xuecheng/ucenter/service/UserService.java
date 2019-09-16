package com.xuecheng.ucenter.service;

import com.xuecheng.framework.domain.ucenter.XcCompanyUser;
import com.xuecheng.framework.domain.ucenter.XcMenu;
import com.xuecheng.framework.domain.ucenter.XcUser;
import com.xuecheng.framework.domain.ucenter.ext.XcUserExt;
import com.xuecheng.ucenter.dao.XcCompanyUserRepository;
import com.xuecheng.ucenter.dao.XcMenuMapper;
import com.xuecheng.ucenter.dao.XcUserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/9/14 13:36
 * @Description:
 */

@Service
public class UserService {

    @Autowired
    private XcUserRepository xcUserRepository;

    @Autowired
    private XcCompanyUserRepository xcCompanyUserRepository;

    @Autowired
    private XcMenuMapper xcMenuMapper;

    /**
     * 根据账户查询用户信息
     * @param username
     * @return
     */
    public XcUserExt getUserext(String username){
        //根据账户查询XcUser信息
        XcUser xcUser = this.findXcUserByUsername(username);
        if(xcUser == null){
            return null;
        }
        //用户id获取
        String id = xcUser.getId();
        //查询用户的所有权限
        List<XcMenu> xcMenuList = xcMenuMapper.selectPermissionByUserId(id);
        //根据用户id查询用户所属的公司Id
        XcCompanyUser xcCompanyUser = xcCompanyUserRepository.findByUserId(id);
        //取到用户的公司Id
        String companyId = null;
        if(xcCompanyUser != null){
            companyId = xcCompanyUser.getCompanyId();
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser,xcUserExt);
        xcUserExt.setCompanyId(companyId);
        //设置权限
        xcUserExt.setPermissions(xcMenuList);
        return xcUserExt;
    }

    //根据账户查询XcUser信息
    public XcUser findXcUserByUsername(String username){

        return xcUserRepository.findByUsername(username);
    }
}
