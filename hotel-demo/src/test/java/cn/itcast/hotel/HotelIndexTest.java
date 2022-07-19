package cn.itcast.hotel;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.MAPPING_TEMPLATE;

/**
 * @author 晓蝈
 * @version 1.0
 */
public class HotelIndexTest {

    private RestHighLevelClient client;

    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://114.116.34.233:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

    @Test
    void testInit() {

    }

    @Test
    void createIndex() throws IOException {
        //1. 创建Request对象
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        //2. 添加请求参数，其实就是DSL的JSON参数部分
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        //3. 发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    @Test
    void deleteIndex() throws IOException {
        //1. 创建Request对象
        DeleteIndexRequest request = new DeleteIndexRequest("hotel");
        //2. 发送请求
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void existsIndex() throws IOException {
        //1. 创建Request对象
        GetIndexRequest request = new GetIndexRequest("hotel");
        //2. 发送请求
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists ? "索引库存在" : "索引库不存在");
    }



}
