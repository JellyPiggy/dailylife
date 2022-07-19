package cn.itcast.hotel;

import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author 晓蝈
 * @version 1.0
 */
public class HotelSearchTest {

    private RestHighLevelClient client;

    @BeforeEach
    void before() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://114.116.34.233:9200")
        ));
    }

    @AfterEach
    void after() throws IOException {
        this.client.close();
    }

    private void handleResponse(SearchResponse response) {
        //结果解析
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到：" + total + "条数据");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            System.out.println(hotelDoc);
        }
    }

    @Test
    void matchAll() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchAllQuery());
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    @Test
    void match() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.matchQuery("all", "外滩"));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);

    }


    @Test
    void multiMatch() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.multiMatchQuery("外滩", "name", "business"));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    @Test
    void term() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.termQuery("city", "上海"));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    @Test
    void range() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().query(QueryBuilders.rangeQuery("price").gte(100).lte(200));
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    @Test
    void bool() throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("city", "上海"));
        boolQuery.filter(QueryBuilders.rangeQuery("price").gte(1500).lte(2000));
        request.source().query(boolQuery);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    @Test
    void pageAndSort() throws IOException {
        int page = 1, size = 5;

        SearchRequest request = new SearchRequest("hotel");

        request.source().query(QueryBuilders.matchAllQuery());
        request.source().sort("price", SortOrder.DESC);
        request.source().from((page - 1) * size).size(size);

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }

    @Test
    void highlight() throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        request.source().query(QueryBuilders.matchQuery("all", "外滩"));
        request.source().highlighter(new HighlightBuilder().field("name")
                .requireFieldMatch(false)
        );

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        SearchHits searchHits = response.getHits();
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 获取文档source
            String json = hit.getSourceAsString();
            // 反序列化
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            //获取高亮结果
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();

            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightField = highlightFields.get("name");
                if (highlightField != null) {
                    String name = highlightField.getFragments()[0].string();
                    hotelDoc.setName(name);
                }
            }

            System.out.println(hotelDoc);
        }
    }

    @Test
    void aggregation() throws IOException {
        //1. 准备Request对象
        SearchRequest request = new SearchRequest("hotel");
        //2. 准备DSL
        request.source().size(0);
        request.source().aggregation(
                AggregationBuilders.terms("brand_agg").field("brand").size(20)
        );
        //3. 发出请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        //4. 解析结果
        Aggregations aggregations = response.getAggregations();

        Terms brandAgg = aggregations.get("brand_agg");

        List<? extends Terms.Bucket> buckets = brandAgg.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String key = bucket.getKeyAsString();
            System.out.println(key);
        }
    }

    @Test
    void suggest() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().suggest(new SuggestBuilder().addSuggestion(
                "suggestions",
                SuggestBuilders.completionSuggestion("suggestion")
                        .prefix("h")
                        .skipDuplicates(true)
                        .size(10)
        ));

        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        Suggest suggest = response.getSuggest();
        CompletionSuggestion suggestion = suggest.getSuggestion("suggestions");
        for (CompletionSuggestion.Entry.Option option : suggestion.getOptions()) {
            String text = option.getText().toString();
            System.out.println(text);
        }
    }


    @Test
    void test() throws IOException {
        SearchRequest request = new SearchRequest("hotel");



        int page = 3, pageSize = 5;
        request.source().query(QueryBuilders.matchQuery("all", "如家"));
        request.source().from((page - 1) * pageSize).size(pageSize);
        request.source().sort("price", SortOrder.DESC);
        request.source().highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));



        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到：" + total + "条数据");

        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField field = highlightFields.get("name");
                if (field != null) {
                    String highlight = field.fragments()[0].string();
                    hotelDoc.setName(highlight);
                }
            }
            System.out.println(hotelDoc);
        }
    }

}
