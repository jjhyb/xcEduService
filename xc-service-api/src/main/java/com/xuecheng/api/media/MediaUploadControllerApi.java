package com.xuecheng.api.media;

import com.xuecheng.framework.domain.media.response.CheckChunkResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.multipart.MultipartFile;
/**
 * @author: huangyibo
 * @Date: 2019/9/11 15:53
 * @Description:
 */

@Api(value="媒资管理接口",description="媒资管理接口，提供文件上传，文件处理等接口")
public interface MediaUploadControllerApi {

    //文件上传前的准备工作，校验文件是否存在
    @ApiOperation("文件上传注册")
    public ResponseResult register(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt);

    @ApiOperation("分块检查")
    public CheckChunkResult checkChunk(String fileMd5, Integer chunk, Integer chunkSize);

    @ApiOperation("上传分块")
    public ResponseResult uploadChunk(MultipartFile file, Integer chunk, String fileMd5);

    @ApiOperation("合并文件")
    public ResponseResult mergeChunks(String fileMd5, String fileName, Long fileSize, String mimetype, String fileExt);
}

