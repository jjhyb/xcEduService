package com.xuecheng.test.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: huangyibo
 * @Date: 2019/9/7 1:35
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFastDfs {

    //上传测试
    @Test
    public void testUpload(){
        try {
            //加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient，用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接TrackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //创建StorageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storageServer);
            //向Storage上传文件
            //本地文件的路径
            String filePath = "G:/vue.jpg";
            //上传成功后拿到文件Id
            String fileId = storageClient1.upload_file1(filePath, "jpg", null);
            System.out.println(fileId);
            //group1/M00/00/00/wKgZhV1yoyiAbYW0AAAeVZz-azM066.jpg
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //下载测试
    @Test
    public void testDownload(){
        try {
            //加载fastdfs-client.properties配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            //定义TrackerClient，用于请求TrackerServer
            TrackerClient trackerClient = new TrackerClient();
            //连接TrackerServer
            TrackerServer trackerServer = trackerClient.getConnection();
            //获取storage
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            //创建StorageClient
            StorageClient1 storageClient1 = new StorageClient1(trackerServer,storageServer);
            //下载文件
            //文件id
            String fileId = "group1/M00/00/00/wKgZhV1yoyiAbYW0AAAeVZz-azM066.jpg";
            byte[] bytes = storageClient1.download_file1(fileId);
            //使用输入流保存文件
            FileOutputStream outputStream = new FileOutputStream(new File("E:/vue.jpg"));
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //查询文件
    @Test
    public void testQueryFile() throws IOException, MyException {
        ClientGlobal.initByProperties("config/fastdfs-client.properties");
        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;
        StorageClient storageClient = new StorageClient(trackerServer,
                storageServer);
        FileInfo fileInfo = storageClient.query_file_info("group1",
                "M00/00/00/wKgZhV1yoyiAbYW0AAAeVZz-azM066.jpg");
        System.out.println(fileInfo);
    }

}
