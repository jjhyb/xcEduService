package com.xuecheng.manage_cms.service.impl;

import com.xuecheng.manage_cms.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 0:02
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class PageServiceImplTest {

    @Autowired
    private PageService pageService;

    @Test
    public void getPageHtmlTest(){
        String html = pageService.getPageHtml("5d6fe59580255d4500b22fc4");
        System.out.println(html);
    }
}
