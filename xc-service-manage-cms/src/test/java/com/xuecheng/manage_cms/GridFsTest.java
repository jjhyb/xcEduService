package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author: huangyibo
 * @Date: 2019/9/4 21:10
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFsTest {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    //存文件
    @Test
    public void testGridFs_course() throws FileNotFoundException {
        //要存储的文件
        File file = new File("G:/course.ftl");
        //定义输入流
        FileInputStream inputStream = new FileInputStream(file);
        ///向GridFS存储文件
        ObjectId id = gridFsTemplate.store(inputStream, "course.ftl");

        //得到文件ID
        System.out.println(id);
    }

    //存文件
    @Test
    public void testGridFs_indexBanner() throws FileNotFoundException {
        //要存储的文件
        File file = new File("G:/index_banner.ftl");
        //定义输入流
        FileInputStream inputStream = new FileInputStream(file);
        ///向GridFS存储文件
        ObjectId id = gridFsTemplate.store(inputStream, "index_banner.ftl");

        //得到文件ID
        System.out.println(id);
    }

    //取文件
    @Test
    public void queryFile() throws IOException {
        //根据文件Id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is("5d6fb9b080255d277cf57863")));
        //打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
        //创建GridFsResource对象，操作流
        GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
        //从流中取数据
        String content = IOUtils.toString(gridFsResource.getInputStream(), "UTF-8");
        System.out.println(content);
    }

    //删除文件
    @Test
    public void testDelFile() throws IOException {
        //根据文件id删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5d6fbd0080255d257ca56c26")));
    }

}
