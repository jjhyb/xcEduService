package com.xuecheng.manage_cms_client.service.impl;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.manage_cms_client.dao.CmsPageRepository;
import com.xuecheng.manage_cms_client.dao.CmsSiteRepository;
import com.xuecheng.manage_cms_client.service.PageService;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Optional;

/**
 * @author: huangyibo
 * @Date: 2019/9/5 22:37
 * @Description:
 */

@Service
public class PageServiceImpl implements PageService {

    private Logger logger = LoggerFactory.getLogger(PageServiceImpl.class);

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsSiteRepository cmsSiteRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 保存html页面到服务器的物理路径
     * @param pageId
     */
    @Override
    public void savePageToServerPath(String pageId) {
        //根据pageId查询cmsPage
        CmsPage cmsPage = this.findCmsPageById(pageId);
        if(cmsPage == null){
            logger.error("PageServiceImpl.findCmsPageById get CmsPage is null,pageId={}",pageId);
//            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
            return;//因为是消息队列监听消费调用此方法，所有抛异常不合适
        }
        //得到html文件的id，从cmsPage中获取htmlFiledId内容
        String htmlFileId = cmsPage.getHtmlFileId();

        //从gridFs中查询html文件
        InputStream inputStream = this.getFileById(htmlFileId);
        if(inputStream == null){
            logger.error("PageServiceImpl.getFileById get InputStream is null,htmlFileId={}",htmlFileId);
//            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
            return;//因为是消息队列监听消费调用此方法，所有抛异常不合适，记录日志
        }

        //得到站点id
        String siteId = cmsPage.getSiteId();
        //得到站点的信息
        CmsSite cmsSite = this.findCmsSiteById(siteId);
        //得到站点的物理路径
        String sitePhysicalPath = cmsSite.getSitePhysicalPath();
        //得到页面的物理路径
        String pagePath = sitePhysicalPath + cmsPage.getPagePhysicalPath() + cmsPage.getPageName();
        logger.info("html保存到服务器的物理路径为，pagePath={}",pagePath);
        //将html页面保存到服务器的物理路径
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(pagePath));
            IOUtils.copy(inputStream,outputStream);
        } catch (Exception e) {
            logger.error("PageServiceImpl.savePageToServerPath 流输出到文件异常,e={}",e);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据文件Id从GridFS中查询文件内容
     * @param fileId
     * @return
     */
    private InputStream getFileById(String fileId){
        //文件对象
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));
        //打开下载流
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //定义GridFSResource
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        try {
            return gridFsResource.getInputStream();
        } catch (IOException e) {
            logger.error("PageServiceImpl.getFileById gridFsResource.getInputStream Exception，fileId={},e={}",fileId,e);
        }
        return null;
    }

    /**
     * 根据页面id查询页面的html文件信息
     * @param pageId
     * @return
     */
    @Override
    public CmsPage findCmsPageById(String pageId) {
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    /**
     * 根据站点id查询站点信息
     * @param siteId
     * @return
     */
    private CmsSite findCmsSiteById(String siteId) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

}
