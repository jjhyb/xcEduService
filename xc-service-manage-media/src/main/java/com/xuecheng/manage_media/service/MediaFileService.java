package com.xuecheng.manage_media.service;

import com.xuecheng.framework.domain.media.MediaFile;
import com.xuecheng.framework.domain.media.request.QueryMediaFileRequest;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.manage_media.dao.MediaFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author: huangyibo
 * @Date: 2019/9/12 2:11
 * @Description:
 */

@Service
public class MediaFileService {

    @Autowired
    private MediaFileRepository mediaFileRepository;

    /**
     * 查询我的媒资列表
     * @param page
     * @param size
     * @param queryMediaFileRequest
     * @return
     */
    public QueryResponseResult findList(int page, int size, QueryMediaFileRequest queryMediaFileRequest) {
        if(queryMediaFileRequest == null){
            queryMediaFileRequest = new QueryMediaFileRequest();
        }
        //条件值对象
        MediaFile mediaFile = new MediaFile();
        if(!StringUtils.isEmpty(queryMediaFileRequest.getTag())){
            mediaFile.setTag(queryMediaFileRequest.getTag());
        }

        if(!StringUtils.isEmpty(queryMediaFileRequest.getFileOriginalName())){
            mediaFile.setFileOriginalName(queryMediaFileRequest.getFileOriginalName());
        }

        if(!StringUtils.isEmpty(queryMediaFileRequest.getProcessStatus())){
            mediaFile.setProcessStatus(queryMediaFileRequest.getProcessStatus());
        }
        //条件匹配器
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("tag",ExampleMatcher.GenericPropertyMatchers.contains())
                .withMatcher("fileOriginalName",ExampleMatcher.GenericPropertyMatchers.contains())
                //精确匹配，如果不设置匹配器，自动默认精确匹配，下面这个匹配器可以不用写
                .withMatcher("processStatus",ExampleMatcher.GenericPropertyMatchers.exact());
        //定义example条件对象
        Example<MediaFile> example = Example.of(mediaFile,exampleMatcher);
        //分页查询对象
        if(page <= 0){
            page = 1;
        }
        if(size <= 0){
            size = 10;
        }
        Pageable pageable = PageRequest.of(page-1,size);
        //分页查询
        Page<MediaFile> pageAll = mediaFileRepository.findAll(example, pageable);
        //总记录数
        long total = pageAll.getTotalElements();
        //数据列表
        List<MediaFile> content = pageAll.getContent();
        //返回的数据集合
        QueryResult<MediaFile> queryResult = new QueryResult<>();
        queryResult.setTotal(total);
        queryResult.setList(content);
        QueryResponseResult queryResponseResult = new QueryResponseResult(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
