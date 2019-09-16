package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: huangyibo
 * @Date: 2019/9/7 16:19
 * @Description:
 */

@Api(value="文件系统服务接口",description = "文件系统服务接口，提供文件的增、删、改、查")
public interface FileSystemControllerApi {

    /**
     * 上传文件
     * @param multipartFile 文件
     * @param filetag 文件标签
     * @param businesskey 业务key
     * @param metadata 元信息,json格式，转成map存储到数据库
     * @return
     */
    @ApiOperation("上传文件接口")
    public UploadFileResult upload(MultipartFile multipartFile, String filetag,
                                   String businesskey, String metadata);

}

