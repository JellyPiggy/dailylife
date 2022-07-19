package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.PageResult;
import cn.itcast.hotel.pojo.RequestParams;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    private RestHighLevelClient client;

    @Override
    public PageResult search(RequestParams params) {
        try {
            SearchRequest request = new SearchRequest("hotel");

            //query
            buildBasicQuery(params, request);

            //from, size
            int page = params.getPage(), pageSize = params.getSize();
            request.source().from((page - 1) * pageSize).size(pageSize);

            //sort
            String location = params.getLocation();
            if (!StringUtils.isEmpty(location)) {
                request.source().sort(SortBuilders
                        .geoDistanceSort("location", new GeoPoint(location))
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS)
                );
            }


            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            return handleResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private PageResult handleResponse(SearchResponse response) {
        List<HotelDoc> resList = new ArrayList<>();
        SearchHits searchHits = response.getHits();
        Long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);

            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                Object distance = sortValues[0];
                hotelDoc.setDistance(distance);
            }

            resList.add(hotelDoc);
        }
        return new PageResult(total, resList);
    }


    public Map<String, List<String>> filters(RequestParams params) {
        try {
            HashMap<String, List<String>> map = new HashMap<>();

            SearchRequest request = new SearchRequest("hotel");

            buildBasicQuery(params, request);

            request.source().size(0);

            request.source().aggregation(AggregationBuilders.terms("cityAgg").field("city").size(10));
            request.source().aggregation(AggregationBuilders.terms("brandAgg").field("brand").size(10));
            request.source().aggregation(AggregationBuilders.terms("starNameAgg").field("starName").size(10));

            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();


            map.put("city", getResList(aggregations, "cityAgg"));
            map.put("brand", getResList(aggregations, "brandAgg"));
            map.put("starName", getResList(aggregations, "starNameAgg"));

            return map;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getSuggestion(String prefix) {
        try {
            SearchRequest request = new SearchRequest("hotel");
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
                    ));
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
            List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
            List<String> list = new ArrayList<>(options.size());
            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                list.add(text);
            }
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private ArrayList<String> getResList(Aggregations aggregations, String agg) {
        ArrayList<String> resList = new ArrayList<>();
        Terms cityTerms = aggregations.get(agg);
        List<? extends Terms.Bucket> buckets = cityTerms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            resList.add(bucket.getKeyAsString());
        }
        return resList;
    }

    private void buildBasicQuery(RequestParams params, SearchRequest request) {
        //1. 构件BoolQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        String key = params.getKey();
        boolQuery.must(StringUtils.isEmpty(key) ?
                QueryBuilders.matchAllQuery() : QueryBuilders.matchQuery("all", key));

        String city = params.getCity();
        if (!StringUtils.isEmpty(city)) {
            boolQuery.filter(QueryBuilders.termQuery("city", city));
        }
        String starName = params.getStarName();
        if (!StringUtils.isEmpty(starName)) {
            boolQuery.filter(QueryBuilders.termQuery("starName", starName));
        }
        String brand = params.getBrand();
        if (!StringUtils.isEmpty(brand)) {
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }
        Integer minPrice = params.getMinPrice(), maxPrice = params.getMaxPrice();
        if (minPrice != null && maxPrice != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
        }

        //2. 算分控制
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                boolQuery,
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                QueryBuilders.termQuery("isAD", true),
                                ScoreFunctionBuilders.weightFactorFunction(10)
                        )
                }
        );

        request.source().query(functionScoreQuery);
    }




    @Override
    public void deleteById(Long id) {
        try {
            DeleteRequest request = new DeleteRequest("hotel", id.toString());
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertOrUpdateById(Long id) {
        try {
            Hotel hotel = getById(id);
            HotelDoc hotelDoc = new HotelDoc(hotel);

            IndexRequest request = new IndexRequest("hotel").id(id.toString());

            request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


  /*
    @Autowired
    private RestHighLevelClient client;

    @Override
    public PageResult search(RequestParams params) {
        //1. 获取请求
        SearchRequest request = new SearchRequest("hotel");
        //2. 准备DSL
        //2.1 关键字搜索
        buildBasicQuery(params, request);

        //2.2 分页
        Integer page = params.getPage();
        Integer pageSize = params.getSize();
        request.source().from((page - 1) * pageSize).size(pageSize);

        //2.3 排序
        String location = params.getLocation();
        if (!StringUtils.isEmpty(location)) {
            request.source().sort(SortBuilders
                    .geoDistanceSort("location", new GeoPoint(location))
                    .order(SortOrder.ASC)
                    .unit(DistanceUnit.KILOMETERS)
            );
        }

        //3. 发送请求，得到响应
        SearchResponse response = null;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //4. 解析响应
        return handleResponse(response);
    }

    private void buildBasicQuery(RequestParams params, SearchRequest request) {
        // 1.构建BooleanQuery
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        String key = params.getKey();
        boolQuery.must(StringUtils.isEmpty(key) ?
                QueryBuilders.matchAllQuery() : QueryBuilders.matchQuery("all", key));

        String city = params.getCity();
        if (!StringUtils.isEmpty(city)) {
            boolQuery.filter(QueryBuilders.termQuery("city", city));
        }

        String brand = params.getBrand();
        if (!StringUtils.isEmpty(brand)) {
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }

        String starName = params.getStarName();
        if (!StringUtils.isEmpty(starName)) {
            boolQuery.filter(QueryBuilders.termQuery("starName", starName));
        }

        Integer minPrice = params.getMinPrice();
        Integer maxPrice = params.getMaxPrice();
        if (minPrice != null && maxPrice != null) {
            boolQuery.filter(QueryBuilders.rangeQuery("price")
                    .gte(minPrice).lte(maxPrice));
        }

        //2. 算分控制
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                //原始查询，相关性算分的查询
                boolQuery,
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        // 其中的一个function score 元素
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                // 过滤条件
                                QueryBuilders.termQuery("isAD", true),
                                // 算分函数
                                ScoreFunctionBuilders.weightFactorFunction(5)
                        )
                }
        );
        request.source().query(functionScoreQuery);
    }

    private PageResult handleResponse(SearchResponse response) {
        //结果解析
        SearchHits searchHits = response.getHits();
        long total = searchHits.getTotalHits().value;
        SearchHit[] hits = searchHits.getHits();
        List<HotelDoc> list = new ArrayList<>();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                Object distance = sortValues[0];
                hotelDoc.setDistance(distance);
            }
            list.add(hotelDoc);
        }
        return new PageResult(total, list);
    }


    @Override
    public Map<String, List<String>> filters(RequestParams params) {
        HashMap<String, List<String>> map = new HashMap<>();
        try {
            SearchRequest request = new SearchRequest("hotel");

            buildBasicQuery(params, request);

            request.source().size(0);
            buildAggregation(request);

            SearchResponse response = client.search(request, RequestOptions.DEFAULT);

            Aggregations aggregations = response.getAggregations();

            List<String> cityResult = getAggResult(aggregations, "cityAgg");
            map.put("city", cityResult);

            List<String> brandResult = getAggResult(aggregations, "brandAgg");
            map.put("brand", brandResult);

            List<String> starNameResult = getAggResult(aggregations, "starNameAgg");
            map.put("starName", starNameResult);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }


    private List<String> getAggResult(Aggregations aggregations, String aggName) {
        Terms cityAgg = aggregations.get(aggName);
        List<? extends Terms.Bucket> cityAggBuckets = cityAgg.getBuckets();
        ArrayList<String> list = new ArrayList<>();
        for (Terms.Bucket bucket : cityAggBuckets) {
            list.add(bucket.getKeyAsString());
        }
        return list;
    }

    private void buildAggregation(SearchRequest request) {
        request.source().aggregation(
                AggregationBuilders.terms("brandAgg").field("brand").size(20)
        );
        request.source().aggregation(
                AggregationBuilders.terms("cityAgg").field("city").size(20)
        );
        request.source().aggregation(
                AggregationBuilders.terms("starNameAgg").field("starName").size(20)
        );
    }


    @Override
    public List<String> getSuggestions(String prefix) {
        try {
            SearchRequest request = new SearchRequest("hotel");
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
            ));

            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Suggest suggest = response.getSuggest();
            CompletionSuggestion suggestion = suggest.getSuggestion("suggestions");
            List<CompletionSuggestion.Entry.Option> options = suggestion.getOptions();

            List<String> results = new ArrayList<>(options.size());

            for (CompletionSuggestion.Entry.Option option : options) {
                String text = option.getText().toString();
                results.add(text);
            }
            return results;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        try {
            DeleteRequest request = new DeleteRequest("hotel", id.toString());
            client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void insertById(Long id) {
        try {
            Hotel hotel = getById(id);
            HotelDoc hotelDoc = new HotelDoc(hotel);

            IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
            request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);

            client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

*/

}
