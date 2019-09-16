package com.xuecheng.manage_media;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/9/11 14:34
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestFile {

    //测试文件分块
    @Test
    public void testChunk() throws IOException {
        //源文件
        File sourceFile = new File("E:/develop/ffmpeg_test/lucene.avi");
        //块文件目录
        String chunkFileFolder = "E:/develop/ffmpeg_test/chunks/";

        //先定义块文件大小
        long chunkFileSize = 1*1024*1024;

        //求出块数
        long chunkFileNum = (long)Math.ceil(sourceFile.length()*1.0 / chunkFileSize);

        //创建读文件对象
        RandomAccessFile raf_Read = new RandomAccessFile(sourceFile,"r");
        //缓冲区byte
        byte[] buffer = new byte[1024];
        for (long i = 0; i < chunkFileNum; i++) {
            File chunkFile = new File(chunkFileFolder+i);
            //创建向块文件的写对象
            RandomAccessFile raf_Write = new RandomAccessFile(chunkFile,"rw");
            int len = 0;
            while((len = raf_Read.read(buffer))!= -1){
                raf_Write.write(buffer,0,len);
                //如果块文件的大小达到了预先规定的文件大小，开始写下一块文件
                if(chunkFile.length() >= chunkFileSize){
                    break;
                }
            }
            raf_Write.close();
        }
        raf_Read.close();;
    }


    //测试文件合并
    @Test
    public void MergeFile() throws IOException {
        //块文件目录
        String chunkFileFolderPath = "E:/develop/ffmpeg_test/chunks/";
        //块文件目录对象
        File chunkFileFolder = new File(chunkFileFolderPath);
        //块文件列表
        File[] files = chunkFileFolder.listFiles();
        //将块文件排序，按名称升序
        List<File> fileList = Arrays.asList(files);
        fileList.sort((file1,file2) -> Integer.parseInt(file1.getName()) - Integer.parseInt(file2.getName()));
        //合并文件
        File mergeFile = new File("E:/develop/ffmpeg_test/lucene_merge.avi");
        //创建新文件
        boolean newFile = mergeFile.createNewFile();
        //创建写对象
        RandomAccessFile raf_Write = new RandomAccessFile(mergeFile,"rw");

        byte[] buffer = new byte[1024];
        for (File file : fileList) {
            //创建读块文件对象
            RandomAccessFile raf_Read = new RandomAccessFile(file,"r");
            int len = 0;
            while ((len = raf_Read.read(buffer)) != -1){
                raf_Write.write(buffer,0,len);
            }
            raf_Read.close();
        }
        raf_Write.close();
    }
}
