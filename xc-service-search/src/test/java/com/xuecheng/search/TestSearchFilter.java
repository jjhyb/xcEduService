package com.xuecheng.search;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @author: huangyibo
 * @Date: 2019/9/9 17:58
 * @Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearchFilter {

    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private RestClient restClient;


    /**
     *  filter
     *  基于BooleanQuery
     *  过虑是针对搜索的结果进行过虑，过虑器主要判断的是文档是否匹配，不去计算和判断文档的匹配度得分，所以过
     *  虑器性能比查询要高，且方便缓存，推荐尽量使用过虑器去实现查询或者过虑器和查询共同使用。
     *  range：范围过虑，保留大于等于80 并且小于等于100的记录。
     *  term：项匹配过虑，保留studymodel等于"201001"的记录。
     * @throws IOException
     * @throws ParseException
     */
    @Test
    public void testFilter() throws IOException, ParseException {
        //搜索请求对象
        SearchRequest searchRequest = new SearchRequest("xc_course");
        //指定类型
        searchRequest.types("doc");
        //搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        //BooleanQuery 搜索方式
        //先定义multiMatchQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery("spring 开发", "name", "description")
                .minimumShouldMatch("50%").field("name", 10);

        //定义一个booleanQuery
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        //定义一个过滤器
        //term：项匹配过虑，保留studymodel等于"201001"的记录。
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel","201001"));
        //range：范围过虑，保留大于等于80 并且小于等于100的记录。
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(80).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);
        //设置源字段过滤，第一个参数表示结果集包括哪些字段，第二个参数表示结果集不包括哪些字段
        searchSourceBuilder.fetchSource(new String[]{"name","studymodel","price","timestamp"},new String[]{});

        //向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        //执行搜索，向es发起http请求
        SearchResponse searchResponse = client.search(searchRequest);
        //搜索结果
        SearchHits hits = searchResponse.getHits();
        //匹配到的总记录数
        long totalHits = hits.getTotalHits();
        //得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (SearchHit searchHit : searchHits) {
            //文档主键
            String id = searchHit.getId();
            //源文档内容
            Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
            String name = (String)sourceAsMap.get("name");
            //由于前面设置了源文档字段过滤，这时description是取不到的
            String description = (String)sourceAsMap.get("description");
            //学习模式
            String studymodel = (String)sourceAsMap.get("studymodel");
            //价格
            Double price = (Double)sourceAsMap.get("price");
            //日期
            Date timestamp = simpleDateFormat.parse((String) sourceAsMap.get("timestamp"));
            System.out.println("id："+id+"，name："+name+"，studymodel："+studymodel+"，price："+price+"，timestamp："+timestamp);
        }
    }

}
