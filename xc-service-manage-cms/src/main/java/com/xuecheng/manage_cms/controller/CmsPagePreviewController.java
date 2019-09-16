package com.xuecheng.manage_cms.controller;

import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_cms.service.PageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 0:46
 * @Description:
 */

@Controller
@RequestMapping("/cms")
public class CmsPagePreviewController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(CmsPagePreviewController.class);

    @Autowired
    private PageService pageService;

    //页面预览
    @GetMapping("/preview/{pageId}")
    public void preview(@PathVariable("pageId") String pageId){
        //执行静态化
        String html = pageService.getPageHtml(pageId);
        if(!StringUtils.isEmpty(html)){
            //通过response对象将内容输出
            try {
                ServletOutputStream outputStream = response.getOutputStream();
                //课程详细页面使用ssi注
                //由于Nginx先请求cms的课程预览功能得到html页面，再解析页面中的ssi标签，这里必须保证cms页面预览返回的
                //页面的Content-Type为text/html;charset=utf-8
                response.setHeader("Content-type","text/html;charset=utf-8");
                outputStream.write(html.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                logger.error("页面静态化异常，e={}",e);
            }
        }
    }

}
