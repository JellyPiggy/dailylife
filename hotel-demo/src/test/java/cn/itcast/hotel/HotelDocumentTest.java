package cn.itcast.hotel;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import cn.itcast.hotel.service.impl.HotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

/**
 * @author 晓蝈
 * @version 1.0
 */
@SpringBootTest
public class HotelDocumentTest {

    @Autowired
    private IHotelService hotelService;

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
    void addDocument() throws IOException {
        // 根据id查询酒店数据
        Hotel hotel = hotelService.getById(61083L);
        // 将酒店数据转换为文档类型
        HotelDoc hotelDoc = new HotelDoc(hotel);

        //1. 创建Request对象
        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
        //2. 准备Json文档
        request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
        //3. 发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void getDocument() throws IOException {
        GetRequest request = new GetRequest("hotel", "61083");
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        String json = response.getSourceAsString();
        HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
        System.out.println(hotelDoc);
    }

    @Test
    void deleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("hotel", "61083");
        client.delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void updateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("hotel", "61083");
        request.doc(
                "price", 998,
                "starName", "四钻"
        );
        client.update(request, RequestOptions.DEFAULT);
    }

    @Test
    void bulkDocument() throws IOException {
        List<Hotel> hotels = hotelService.list();

        BulkRequest request = new BulkRequest();

        for (Hotel hotel : hotels) {
            HotelDoc hotelDoc = new HotelDoc(hotel);
            request.add(new IndexRequest("hotel").id(hotelDoc.getId().toString())
                    .source(JSON.toJSONString(hotelDoc), XContentType.JSON)
            );
        }
        client.bulk(request, RequestOptions.DEFAULT);
    }

    @Test
    void test() throws IOException {
        List<Hotel> hotels = hotelService.list();

        BulkRequest request = new BulkRequest();

        for (Hotel hotel : hotels) {
            HotelDoc hotelDoc = new HotelDoc(hotel);
            request.add(new IndexRequest("hotel")
                    .id(hotelDoc.getId().toString())
                    .source(JSON.toJSONString(hotelDoc), XContentType.JSON));
        }

        client.bulk(request, RequestOptions.DEFAULT);
    }

}
