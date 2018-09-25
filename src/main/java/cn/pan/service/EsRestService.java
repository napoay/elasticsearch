package cn.pan.service;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class EsRestService {

    public RestHighLevelClient getRestClient() {

        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        return client;
    }

    /**
     * 初始化索引
     *
     * @param indexName
     * @param typeName
     * @param shardNum
     * @param replicNum
     * @param builder
     * @return
     */

    public Boolean initIndex(String indexName,
                             String typeName,
                             int shardNum,
                             int replicNum,
                             XContentBuilder builder) {
        //创建索引
        RestHighLevelClient client = getRestClient();
        CreateIndexRequest request = new CreateIndexRequest(indexName);

        //设置分片和副本
        request.settings(Settings.builder()
                .put("index.number_of_shards", shardNum)
                .put("index.number_of_replicas", replicNum)
        );

        request.mapping(typeName, builder);
        CreateIndexResponse createIndexResponse = null;
        try {
            createIndexResponse = client.indices().create(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return createIndexResponse.isAcknowledged();
    }

    /**
     * @param indexName
     * @param typeName
     * @param jsonString
     * @return
     */

    public boolean indexDoc(String indexName,
                            String typeName,
                            String id,
                            String jsonString
    ) {
        RestHighLevelClient client = getRestClient();
        IndexRequest indexRequest = new IndexRequest(indexName, typeName, id)
                .source(jsonString, XContentType.JSON);
        try {
            IndexResponse indexResponse = client.index(indexRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * @param indexName
     * @param typeName
     * @param docList
     * @return
     */

    public boolean indexDoc(String indexName,
                            String typeName,
                            List docList
    ) {
        RestHighLevelClient client = getRestClient();
        BulkRequest bulkRequest = new BulkRequest();


        Iterator<String> iter = docList.iterator();

        while (iter.hasNext()) {
            String jsonString = iter.next();
            IndexRequest indexRequest = new IndexRequest(indexName, typeName)
                    .source(jsonString, XContentType.JSON);
            bulkRequest.add(indexRequest);
        }
        try {
            BulkResponse bulkResponse = client.bulk(bulkRequest);
            if (bulkResponse.hasFailures()) {
                System.out.println("批量索引失败");

                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    public ArrayList<Map<String, Object>> searchDocs(String indics,
                                                     String keyword,
                                                     String[] fieldNames,
                                                     int pageNum,
                                                     int pageSize) {

        SearchRequest searchRequest = new SearchRequest(indics);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        MultiMatchQueryBuilder multiMatchQuery = QueryBuilders
                .multiMatchQuery(keyword, fieldNames)
                .operator(Operator.AND);

        HighlightBuilder highlightBuilder = new HighlightBuilder();

        HighlightBuilder.Field highlightTitle =
                new HighlightBuilder.Field("title");
        highlightBuilder.field(highlightTitle);

        HighlightBuilder.Field highlightFilecontent = new HighlightBuilder.Field("filecontent");
        highlightBuilder.field(highlightFilecontent);

        highlightBuilder
                .preTags("<span style=color:red>")
                .postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        searchSourceBuilder.query(multiMatchQuery);
        searchSourceBuilder.from((pageNum-1)*pageSize);
        searchSourceBuilder.size(pageSize);
        searchRequest.source(searchSourceBuilder);
        ArrayList<Map<String, Object>> resultList = new ArrayList<>();


        try {
            SearchResponse searchResponse = getRestClient()
                    .search(searchRequest);
            SearchHits hits = searchResponse.getHits();
            System.out.println("共搜索到: " + hits.totalHits);
            SearchHit[] searchHits = hits.getHits();
            for (SearchHit hit : searchHits) {

                Map<String, Object> sourceAsMap = hit.getSourceAsMap();
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField hTitle = highlightFields.get("title");

                if (hTitle != null) {
                    String hTitleText = "";
                    Text[] fragments = hTitle.fragments();
                    for (Text text : fragments) {
                        hTitleText += text;
                    }
                    sourceAsMap.put("title", hTitleText);
                }
                HighlightField hFilecontent = highlightFields.get("filecontent");
                if (hFilecontent != null) {
                    String hFilecontentText = "";
                    Text[] fragments = hFilecontent.fragments();
                    for (Text text : fragments) {
                        hFilecontentText += text;
                    }
                    sourceAsMap.put("filecontent", hFilecontentText);
                }
                resultList.add(sourceAsMap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        return resultList;
    }
}
