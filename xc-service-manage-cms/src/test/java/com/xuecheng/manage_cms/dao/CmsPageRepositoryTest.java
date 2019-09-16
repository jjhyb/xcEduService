package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author: huangyibo
 * @Date: 2019/9/2 19:45
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    private Logger logger = LoggerFactory.getLogger(CmsPageRepositoryTest.class);

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
    public void testFindAll(){
        List<CmsPage> list = cmsPageRepository.findAll();
        logger.info("总记录数："+list.size());
    }

    //分页测试          
    @Test
    public void testFindPage() {
        int page = 0;//从0开始         
        int size = 10;//每页记录数         
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        logger.info("分页数据："+all);
    }

    //分页测试          
    @Test
    public void testFindAllByeExample() {
        int page = 0;//从0开始         
        int size = 10;//每页记录数
        //条件值对象
        CmsPage cmsPage = new CmsPage();
        //要查询的站点id的数据
//        cmsPage.setSiteId("5b4b1d8bf73c6623b03f8cec");
        //设置模板id条件
//        cmsPage.setTemplateId("18re1re1212e12");
        //设置页面的别名
        cmsPage.setPageAliase("轮播");

        //条件匹配器
        ExampleMatcher matcher = ExampleMatcher.matching();
        matcher = matcher.withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
        //ExampleMatcher.GenericPropertyMatchers.contains()包含关键字
        //ExampleMatcher.GenericPropertyMatchers.startsWith()//前缀匹配
        //ExampleMatcher.GenericPropertyMatchers.endsWith()//末尾匹配
        //ExampleMatcher.GenericPropertyMatchers.caseSensitive()//区分大小写匹配
        //ExampleMatcher.GenericPropertyMatchers.exact()//精确匹配
        //ExampleMatcher.GenericPropertyMatchers.ignoreCase()//忽略大小写匹配

        //定义Example
        Example<CmsPage> example = Example.of(cmsPage,matcher);

        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        List<CmsPage> content = all.getContent();
        System.out.println(content);
    }

    //修改
    @Test
    public void testUpdate() {
        //查询对象
        Optional<CmsPage> optional = cmsPageRepository.findById("5b4b1d8bf73c6623b03f8cec");
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();
            //设置要修改值
            cmsPage.setPageAliase("test01");
            //...
            //修改
            CmsPage save = cmsPageRepository.save(cmsPage);
            System.out.println(save);
        }

    }

    //根据页面名称查询
    @Test
    public void testfindByPageName(){
        CmsPage cmsPage = cmsPageRepository.findByPageName("测试页面");
        System.out.println(cmsPage);
    }
}
