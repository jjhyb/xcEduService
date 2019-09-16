package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.course.TeachplanMediaPub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/9/10 16:37
 * @Description:
 */

@Service
public class EsCourseService {

    private Logger logger = LoggerFactory.getLogger(EsCourseService.class);

    @Value("${xuecheng.course.index}")
    private String index;

    @Value("${xuecheng.media.index}")
    private String mediaIndex;

    @Value("${xuecheng.course.type}")
    private String type;

    @Value("${xuecheng.media.type}")
    private String mediaType;

    @Value("${xuecheng.course.source_field}")
    private String source_field;

    @Value("${xuecheng.media.source_field}")
    private String media_source_field;

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    /**
     * 课程搜索
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    public QueryResponseResult<CoursePub> list(int page, int size, CourseSearchParam courseSearchParam) {
        if(courseSearchParam == null){
            courseSearchParam = new CourseSearchParam();
        }
        //创建搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //设置搜索的类型
        searchRequest.types(type);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //过滤源字段
        String[] source_field_array = source_field.split(",");
        searchSourceBuilder.fetchSource(source_field_array,new String[]{});

        //创建booleanQuery查询对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //搜索条件
        //根据关键字搜索
        if(!StringUtils.isEmpty(courseSearchParam.getKeyword())){
            //匹配关键字
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), "name", "description", "teachplan")
                    .minimumShouldMatch("70%")//设置占比
                    .field("name", 10);//设置权重
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        if(!StringUtils.isEmpty(courseSearchParam.getMt())){
            //根据1级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt",courseSearchParam.getMt()));
        }

        if(!StringUtils.isEmpty(courseSearchParam.getSt())){
            //根据2级分类
            boolQueryBuilder.filter(QueryBuilders.termQuery("st",courseSearchParam.getSt()));
        }

        if(!StringUtils.isEmpty(courseSearchParam.getGrade())){
            //根据难度等级
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade",courseSearchParam.getGrade()));
        }


        //设置boolQueryBuilder到searchSourceBuilder中
        searchSourceBuilder.query(boolQueryBuilder);
        //设置分页
        if(page <= 0){
            page = 1;
        }
        if(size <= 0){
            page = 8;
        }
        int from =  (page - 1) * size;//起始记录下标
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(size);

        //设置高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<em class='eslight'>");
        highlightBuilder.postTags("</em>");
        //设置高亮字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        searchSourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(searchSourceBuilder);

        QueryResult<CoursePub> queryResult = new QueryResult<CoursePub>();
        List<CoursePub> list = new ArrayList<>();
        try {
            //执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            //获取响应结果
            SearchHits searchHits = searchResponse.getHits();
            //匹配的总记录数
            long totalHits = searchHits.getTotalHits();
            queryResult.setTotal(totalHits);
            //
            SearchHit[] searchHitsArr = searchHits.getHits();
            for (SearchHit hit : searchHitsArr) {
                CoursePub coursePub = new CoursePub();
                //源文档
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出Id
                String id = (String)sourceAsMap.get("id");
                coursePub.setId(id);
                //取出name
                String name = (String)sourceAsMap.get("name");
                //取出高亮字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                if(!CollectionUtils.isEmpty(highlightFields)){
                    HighlightField highlightField = highlightFields.get("name");
                    if(highlightField != null){
                        Text[] fragments = highlightField.getFragments();
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Text text : fragments) {
                            stringBuilder.append(text.toString());
                        }
                        name = stringBuilder.toString();
                    }
                }
                coursePub.setName(name);
                //图片
                String pic = (String) sourceAsMap.get("pic");
                coursePub.setPic(pic);
                //价格
                Double price = null;
                try {
                    if(sourceAsMap.get("price") != null){
                        price = (Double) sourceAsMap.get("price");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice(price);
                //旧价格
                Double price_old = null;
                try {
                    if(sourceAsMap.get("price_old") != null){
                        price_old = (Double) sourceAsMap.get("price_old");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                coursePub.setPrice_old(price_old);
                list.add(coursePub);
            }
            queryResult.setList(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        QueryResponseResult<CoursePub> coursePubQueryResponseResult =
                new QueryResponseResult<CoursePub>(CommonCode.SUCCESS,queryResult);
        return coursePubQueryResponseResult;
    }

    /**
     * 根据课程id查询课程信息
     * 使用ES客户端向ES请求查询索引信息
     * @param id
     * @return
     */
    public Map<String, CoursePub> getAll(String id) {
        //定义一个搜索请求对象
        SearchRequest searchRequest = new SearchRequest(index);
        //指定type
        searchRequest.types(type);
        //定义SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termQuery
        searchSourceBuilder.query(QueryBuilders.termQuery("id",id));
        //过滤源字段，这里不用设置过滤了，取出所有字段
        //searchSourceBuilder.fetchSource();
        searchRequest.source(searchSourceBuilder);
        //最终要返回的课程信息
        Map<String, CoursePub> map = new HashMap<>();
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits searchHits = searchResponse.getHits();
            SearchHit[] hits = searchHits.getHits();
            for (SearchHit hit : hits) {
                CoursePub coursePub = new CoursePub();
                //获取到源文档内容
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                String courseId = (String) sourceAsMap.get("id");
                String name = (String) sourceAsMap.get("name");
                String grade = (String) sourceAsMap.get("grade");
                String charge = (String) sourceAsMap.get("charge");
                String pic = (String) sourceAsMap.get("pic");
                String description = (String) sourceAsMap.get("description");
                String teachplan = (String) sourceAsMap.get("teachplan");
                coursePub.setId(courseId);
                coursePub.setName(name);
                coursePub.setPic(pic);
                coursePub.setGrade(grade);
                coursePub.setTeachplan(teachplan);
                coursePub.setDescription(description);
                map.put(courseId,coursePub);
            }
        } catch (IOException e) {
            logger.error("搜索服务异常，e={}",e);
        }
        return map;
    }

    /**
     * 根据多个课程计划id查询媒资信息
     * @param teachplanIds
     * @return
     */
    public QueryResponseResult<TeachplanMediaPub> getmedia(String[] teachplanIds){
        //定义一个搜索请求对象
        SearchRequest searchRequest = new SearchRequest(mediaIndex);
        //指定type
        searchRequest.types(mediaType);
        //定义SearchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置使用termsQuery根据多个id查询
        searchSourceBuilder.query(QueryBuilders.termsQuery("teachplan_id",teachplanIds));
        //过滤源字段，这里不用设置过滤了，取出所有字段
        String[] mediaFields = media_source_field.split(",");
        searchSourceBuilder.fetchSource(mediaFields,new String[]{});
        searchRequest.source(searchSourceBuilder);
        List<TeachplanMediaPub> teachplanMediaPubList = new ArrayList<>();
        long total = 0;
        try {
            //执行搜索
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
            SearchHits searchHits = searchResponse.getHits();
            total = searchHits.getTotalHits();
            SearchHit[] hits = searchHits.getHits();
            for (SearchHit hit : hits) {
                TeachplanMediaPub teachplanMediaPub = new TeachplanMediaPub();
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                //取出课程计划媒资信息
                String courseid = (String) sourceAsMap.get("courseid");
                String media_id = (String) sourceAsMap.get("media_id");
                String media_url = (String) sourceAsMap.get("media_url");
                String teachplan_id = (String) sourceAsMap.get("teachplan_id");
                String media_fileoriginalname = (String) sourceAsMap.get("media_fileoriginalname");
                teachplanMediaPub.setCourseId(courseid);
                teachplanMediaPub.setMediaUrl(media_url);
                teachplanMediaPub.setMediaFileOriginalName(media_fileoriginalname);
                teachplanMediaPub.setMediaId(media_id);
                teachplanMediaPub.setTeachplanId(teachplan_id);
                //将数据加入列表
                teachplanMediaPubList.add(teachplanMediaPub);
            }
        } catch (IOException e) {
            logger.error("根据多个课程计划id查询媒资信息异常,e={}",e);
        }
        //构建返回课程媒资信息对象
        QueryResult<TeachplanMediaPub> queryResult = new QueryResult<>();
        queryResult.setList(teachplanMediaPubList);
        QueryResponseResult<TeachplanMediaPub> queryResponseResult =
                new QueryResponseResult<TeachplanMediaPub>(CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }
}
