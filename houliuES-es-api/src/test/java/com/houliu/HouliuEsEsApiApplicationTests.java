package com.houliu;

import com.alibaba.fastjson.JSON;
import com.houliu.pojo.User;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class HouliuEsEsApiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    //测试索引的创建  Request
    @Test
    void testCreateIndex() throws IOException {
        //1。创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("houliu_index");
        //2.客户端执行请求,请求后获得响应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(createIndexResponse);
    }

    //测试获取索引，判断索引是否存在
    @Test
    void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("houliu_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(exists);
    }

    //测试删除索引
    @Test
    void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("houliu_index");
        //删除
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);

        System.out.println(delete.isAcknowledged());
    }

    //测试添加文档
    @Test
    void testAddDocument() throws IOException {
        //创建对象
        User user = new User("houliu", 3);
        //创建请求
        IndexRequest indexRequest = new IndexRequest("houliu_index");
        //规则，put/houliu_index/_doc/1
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueMillis(1L));
        indexRequest.timeout("1s");
        //将数据放入请求
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求,获取响应的结果
        IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(indexResponse.toString());
        System.out.println(indexResponse.status());  //对应命令返回的状态
    }

    //测试获取文档,判断文档是否存在
    @Test
    void testGetIsExist() throws IOException {
        GetRequest getRequest = new GetRequest("houliu_index", "1");
        //不获取返回的source的上下文
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");
        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);

        System.out.println(exists);

    }

    //测试获取文档的信息
    @Test
    void testGetDocument() throws IOException {
        GetRequest getRequest = new GetRequest("houliu_index", "1");
        GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);

        System.out.println(documentFields.getSourceAsString());  //打印文档的内容
    }

    //测试更新文档
    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("houliu_index", "1");
        updateRequest.timeout("1s");
        User user = new User("陈俊安", 12);
        updateRequest.doc(JSON.toJSONString(user),XContentType.JSON);  //修改内容
        //执行更新操作
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

        System.out.println(updateResponse.status());

    }

    //删除文档记录
    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("houliu_index", "3");
        deleteRequest.timeout("1s");
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(deleteResponse.status());
    }

    //特殊的，真实的项目一般都会批量的插入数据
    @Test
    void testbatchDeleteDocument() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        List<User> userList = new ArrayList<>();
        userList.add(new User("zhangsan",3));
        userList.add(new User("lisi",4));
        userList.add(new User("wangwu",5));
        userList.add(new User("houliu2",22));
        userList.add(new User("luoyonghao",33));
        //批处理执行
        for (int i = 0; i < userList.size(); i++) {
            bulkRequest.add(
                    new IndexRequest("houliu_index")
                            .id(String.valueOf(i+1))
                            .source(JSON.toJSONString(userList.get(i)),XContentType.JSON));
        }
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);

        System.out.println(bulkResponse.hasFailures()); //输出是否有失败，返回false表示成功

    }

    //查询
    //SearchRequest : 搜索请求
    //SearchSourceBuilder：条件构造
    //highlighterBuilder: 构造高亮
    //termQueryBuilder： 精确查询
    //matchAllQueryBuilder :全查询
    @Test
    void testSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("houliu_index");
        //构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.highlighter();
        //查询条件，可以使用 QueryBuilders 工具来实现
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "zhangsan");
        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSON.toJSONString(searchResponse.getHits()));
        System.out.println("===================================================");
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }

    }

}
