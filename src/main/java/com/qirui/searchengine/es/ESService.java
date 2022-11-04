package com.qirui.searchengine.es;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.qirui.searchengine.bean.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ESService {
    @Resource
    private RestHighLevelClient restHighLevelClient;

    public ResponseBean createIndex(String indexName) {
        try {
            XContentBuilder builder = XContentFactory.jsonBuilder().startObject().field("properties").startObject()
//                    .field("id").startObject().field("type", "text").endObject()
                    .field("title").startObject().field("index", "true").field("type", "text")
                    .field("analyzer", "ik_max_word").field("search_analyzer", "ik_max_word")
                    .field("term_vector", "with_positions_offsets_payloads").endObject()
                    .field("brief").startObject().field("index", "true").field("type", "text")
                    .field("analyzer", "ik_max_word").field("search_analyzer", "ik_max_word")
                    .field("term_vector", "with_positions_offsets_payloads").endObject()
                    .field("info").startObject().field("index", "true").field("type", "text")
                    .field("analyzer", "ik_max_word").field("search_analyzer", "ik_max_word")
                    .field("term_vector", "with_positions_offsets_payloads").endObject()
                    .field("body").startObject().field("index", "true").field("type", "text")
                    .field("analyzer", "ik_max_word").field("search_analyzer", "ik_max_word")
                    .field("term_vector", "with_positions_offsets_payloads").endObject()
                    .field("url").startObject().field("type", "keyword").endObject()
                    .field("indextime").startObject().field("type", "date")
                    .field("format", "strict_date_optional_time||epoch_millis").endObject()
                    .endObject()
                    .endObject();

            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName.toLowerCase());
            createIndexRequest.settings(Settings.builder().put("index.number_of_shards", 5)
                    .put("index.number_of_replicas", 0)
                    .put("index.refresh_interval", "-1"));
            createIndexRequest.mapping(builder);

            CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            boolean acknowledged = createIndexResponse.isAcknowledged();
            if (acknowledged) {
                return new ResponseBean(200, "创建成功", null);
            } else {
                return new ResponseBean(1002, "创建失败", null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseBean query(String param, int from) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            SearchRequest searchRequest = new SearchRequest("wiki_es");

            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.from(from);
            sourceBuilder.size(10);
            sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", "query:'" + param + "','boost':2"));
            boolQueryBuilder.should(QueryBuilders.matchQuery("brief", "query:'" + param + "','boost':1.2"));
            boolQueryBuilder.filter(QueryBuilders.multiMatchQuery("query':'" + param + "'", "info", "body"));

            sourceBuilder.query(boolQueryBuilder);

            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.postTags("</span>");
            highlightBuilder.preTags("<span class='highlight'>");
            highlightBuilder.fragmentSize(160);
            highlightBuilder.noMatchSize(120);
            highlightBuilder.numOfFragments(1);
            highlightBuilder.field("title");
            highlightBuilder.field("brief");
            highlightBuilder.field("body");
            sourceBuilder.highlighter(highlightBuilder);

            TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("id_aggs").field("id").size(10);
            sourceBuilder.aggregation(termsAggregationBuilder);
            searchRequest.source(sourceBuilder);

            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            SearchHit[] hitArr = hits.getHits();
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < hitArr.length; i++) {
                JSONObject jsonObject = new JSONObject();
                Map<String, HighlightField> highlightFields = hitArr[i].getHighlightFields();
                HighlightField titleField = highlightFields.get("title");
                jsonObject.put("title", getText(titleField));
                jsonObject.put("url", hitArr[i].getSourceAsMap().get("url"));
                HighlightField briefField = highlightFields.get("brief");
                jsonObject.put("brief", getText(briefField));
                HighlightField bodyField = highlightFields.get("body");
                jsonObject.put("body", getText(bodyField));
                jsonObject.put("source", hitArr[i].getScore());
                jsonArray.add(jsonObject);
            }
            map.put("wikiList", jsonArray);
            map.put("wikiCount", hits.getTotalHits());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ResponseBean(200, "success", map);
    }

    public StringBuilder getText(HighlightField field) {
        if (field != null) {
            StringBuilder tmp = new StringBuilder();
            for (Text text : field.fragments()) {
                tmp.append(text);
            }
            return tmp;
        }
        return null;
    }
}
