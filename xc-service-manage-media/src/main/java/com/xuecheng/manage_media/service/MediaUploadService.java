package com.xuecheng.manage_media.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.domain.media.response.MediaCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_media.config.RabbitMQConfig;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

/**
 * @author: huangyibo
 * @Date: 2019/9/11 16:06
 * @Description:
 */

@Service
public class MediaUploadService {

    private Logger logger = LoggerFactory.getLogger(MediaUploadService.class);

    @Autowired
    private MediaFileRepository mediaFileRepository;

    @Value("${xc-service-manage-media.upload-location}")
    private String uploadLocation;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${xc-service-manage-media.mq.routingkey-media-video}")
    private String routingkey_media_video;

    /**
     * 文件上传前的注册，检查文件是否存在
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     * @param fileMd5 文件md5值
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //1、检查文件子啊磁盘上是否存在
        //文件所属目录路径
        String fileFolderPath = this.getFileFolderPath(fileMd5);
        //文件路径
        String filePath = this.getFilePath(fileMd5, fileExt);
        File file = new File(filePath);
        boolean exists = file.exists();
        //2、检查文件信息在mongoDB中是否存在
        Optional<MediaFile> optional = mediaFileRepository.findById(fileMd5);
        if(exists && optional.isPresent()){
            //表示文件已存在
            ExceptionCast.cast(MediaCode.CHUNK_FILE_EXIST_CHECK);
        }
        //文件不存在时，做一些准备工作
        File fileFolder = new File(fileFolderPath);
        if(!fileFolder.exists()){
            fileFolder.mkdirs();
        }

        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 根据文件md5得到文件路径
     * 规则：
     * 一级目录：md5的第一个字符
     * 二级目录：md5的第二个字符
     * 三级目录：md5
     * 文件名：md5+文件扩展名
     * @param fileMd5 文件md5值
     * @param fileExt 文件扩展名
     * @return 文件路径
     */
    private String getFilePath(String fileMd5,String fileExt){
        String filePath = uploadLocation+fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) +
                "/" + fileMd5 + "/" + fileMd5 + "." + fileExt;
        return filePath;
    }

    /**
     * 文件所属目录路径
     * @param fileMd5
     * @return
     */
    private String getFileFolderPath(String fileMd5){
        String filePath = uploadLocation+fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/";
        return filePath;
    }

    /**
     * 文件所属目录路径
     * @param fileMd5
     * @return
     */
    private String getChunkFileFolderPath(String fileMd5){
        String filePath = uploadLocation+fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/chunk/";
        return filePath;
    }

    /**
     * 分块检查
     * @param fileMd5 文件md5值
     * @param chunk 块的下标
     * @param chunkSize 块的大小
     * @return
     */
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Integer chunkSize){
        //检查分块文件是否存在
        //得到分块文件的所属目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //块文件
        File chunkFile = new File(chunkFileFolderPath + chunk);
        if (chunkFile.exists()){
            //块文件存在
            return new CheckChunkResult(CommonCode.SUCCESS,true);
        }
        //块文件不存在
        return new CheckChunkResult(CommonCode.SUCCESS,false);
    }

    /**
     * 上传分块
     * @param file
     * @param chunk
     * @param fileMd5
     * @return
     */
    public ResponseResult uploadChunk(MultipartFile file, Integer chunk, String fileMd5) {
        //检查分块目录，
        //得到分块目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        //得到分块文件的路径
        String chunkFilePath = chunkFileFolderPath + chunk;
        File chunkFileFolder = new File(chunkFileFolderPath);
        //如果不存在则要自动创建
        if(!chunkFileFolder.exists()){
            chunkFileFolder.mkdirs();
        }
        //得到上传文件的输入流
        InputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            inputStream = file.getInputStream();
            outputStream = new FileOutputStream(new File(chunkFilePath));
            IOUtils.copy(inputStream,outputStream);
        } catch (IOException e) {
            logger.error("上传分块文件异常，e={}",e);
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                logger.error("关闭输入流异常，e={}",e);
            }
            try {
                outputStream.close();
            } catch (IOException e) {
                logger.error("关闭输出流异常，e={}",e);
            }
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 合并文件
     * @param fileMd5
     * @param fileName
     * @param fileSize
     * @param mimetype
     * @param fileExt
     * @return
     */
    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt) {
        //1、合并所有分块
        //得到分块文件的所属目录
        String chunkFileFolderPath = this.getChunkFileFolderPath(fileMd5);
        File chunkFileFolder = new File(chunkFileFolderPath);
        //分块文件列表
        File[] files = chunkFileFolder.listFiles();
        //创建一个合并文件
        String filePath = this.getFilePath(fileMd5, fileExt);
        File mergeFile = new File(filePath);

        //执行合并
        mergeFile = this.mergeFile(Arrays.asList(files), mergeFile);
        if(mergeFile == null){
            logger.error("MediaUploadService.mergeChunks.mergeFile is null 合并文件失败");
            //合并文件失败
            ExceptionCast.cast(MediaCode.MERGE_FILE_FAIL);
        }
        //2、校验文件的MD5值是否和前端传入的MD5值相同
        boolean checkFileMd5 = checkFileMd5(mergeFile, fileMd5);
        if(!checkFileMd5){
            //校验文件失败
            ExceptionCast.cast(MediaCode.MERGE_FILE_CHECKFAIL);
        }
        //3、将文件的信息写入Mongodb
        //将文件信息保存到数据库
        MediaFile mediaFile = new MediaFile();
        mediaFile.setFileId(fileMd5);
        mediaFile.setFileName(fileMd5+"."+fileExt);
        mediaFile.setFileOriginalName(fileName);
        //文件路径保存相对路径
        String filePath1 = fileMd5.substring(0,1) + "/" + fileMd5.substring(1,2) + "/" + fileMd5 + "/";
        mediaFile.setFilePath(filePath1);
        mediaFile.setFileSize(fileSize);
        mediaFile.setUploadTime(new Date());
        mediaFile.setMimeType(mimetype);
        mediaFile.setFileType(fileExt);
        //状态为上传成功
        mediaFile.setFileStatus("301002");
        mediaFileRepository.save(mediaFile);
        //向MQ发送视频处理消息
        sendProcessVideoMsg(mediaFile.getFileId());
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 向MQ发送视频处理消息
     * @param mediaId 文件id
     * @return
     */
    public ResponseResult sendProcessVideoMsg(String mediaId){
        Optional<MediaFile> optional = mediaFileRepository.findById(mediaId);
        if(!optional.isPresent()){
            return new ResponseResult(CommonCode.FAIL);
        }
        //向MQ发送视频处理消息
        //构造消息内容
        Map<String,String> map = new HashMap<>();
        map.put("mediaId",mediaId);
        String message = JSON.toJSONString(map);
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EX_MEDIA_PROCESSTASK,routingkey_media_video,message);
        } catch (AmqpException e) {
            logger.error("消息发送失败,message={}",message);
            return new ResponseResult(CommonCode.FAIL);
        }
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 校验文件md5值
     * @param mergeFile
     * @param fileMd5
     * @return
     */
    private boolean checkFileMd5(File mergeFile,String fileMd5){
        try {
            //创建文件的输入流
            FileInputStream inputStream = new FileInputStream(mergeFile);
            //得到文件的md5值
            String md5Hex = DigestUtils.md5DigestAsHex(inputStream);
            //和传入的md5比较
            if(fileMd5.equalsIgnoreCase(md5Hex)){
                return true;
            }
        } catch (Exception e) {
            logger.error("获取文件mergeFile的md5值异常，e={}",e);
        }
        return false;
    }

    /**
     * 合并文件
     * @param chunkFileList
     * @param mergeFile
     * @return
     */
    private File mergeFile(List<File> chunkFileList, File mergeFile){
        try {
            //如果合并的文件已存在则删除
            if(mergeFile.exists()){
                mergeFile.delete();
            }
            //创建一个新文件
            mergeFile.createNewFile();
            //对文件进行排序
            chunkFileList.sort((file1,file2) -> Integer.parseInt(file1.getName()) - Integer.parseInt(file2.getName()));
            RandomAccessFile raf_write = new RandomAccessFile(mergeFile,"rw");
            byte[] buffer = new byte[1024];
            for (File chunkFile : chunkFileList) {
                RandomAccessFile raf_read = new RandomAccessFile(chunkFile,"r");
                int len = 0;
                while ((len = raf_read.read(buffer)) != -1){
                    raf_write.write(buffer,0,len);
                }
                raf_read.close();
            }
            raf_write.close();
            return mergeFile;
        } catch (IOException e) {
            logger.error("合并文件异常，e={}",e);
        }
        return null;
    }
}
