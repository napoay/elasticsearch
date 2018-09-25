package cn.pan;

import cn.pan.congiguration.EsConfiguration;
import cn.pan.model.UserDoc;
import cn.pan.service.EsRestService;
import cn.pan.service.TikaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EsfilesearchApplicationTests {

    @Autowired
    TikaService tikaService;


    @Autowired
    EsConfiguration esConfiguration;

    @Autowired
    EsRestService restService;

    @Test
    public void contextLoads() {
    }


    /**
     * 利用Tika读取files目录下的文件
     */
    @Test
    public void test1() {
        Resource resource = new ClassPathResource("files/如何使用JSON.doc");
        try {
            File file = resource.getFile();

            System.out.println(tikaService.parserExtraction(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试es配置是否成功
     */

    @Test
    public void test2() {
        System.out.println(esConfiguration.getHost());
    }


    @Test
    public void test3() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        System.out.println(client);
    }

    @Test
    public void test4() {
        //设置mapping
        XContentBuilder builder = null;
        try {
            builder = XContentFactory.jsonBuilder();
            builder.startObject();
            {
                builder.startObject("properties");
                {
                    builder.startObject("title");
                    {
                        builder.field("type", "text");
                        builder.field("analyzer", "ik_max_word");
                    }
                    builder.endObject();
                    builder.startObject("filecontent");
                    {
                        builder.field("type", "text");
                        builder.field("analyzer", "ik_max_word");
                        builder.field("term_vector", "with_positions_offsets");
                    }
                    builder.endObject();
                }
                builder.endObject();
            }
            builder.endObject();

            Boolean isSuccess = restService.initIndex("userdoc",
                    "file", 3, 0, builder);

            System.out.println(isSuccess);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test5() {
        RestHighLevelClient client = restService.getRestClient();

        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";

        IndexRequest indexRequest = new IndexRequest("userdoc",
                "file", "1")
                .source(jsonString, XContentType.JSON);

        try {
            IndexResponse indexrespone = client.index(indexRequest);

            System.out.println(indexrespone.status());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导数据
     */
    @Test
    public void test6() {
        Resource resource = new ClassPathResource("files");
        ObjectMapper objMapper = new ObjectMapper();

        try {
            File fileDir = resource.getFile();
            System.out.println(fileDir.isDirectory());
            if (fileDir.exists() && fileDir.isDirectory()) {
                File[] allFiles = fileDir.listFiles();
                for (File f : allFiles) {
                    UserDoc userDoc = tikaService.parserExtraction(f);
                    String json = objMapper.writeValueAsString(userDoc);
                    restService.indexDoc("userdoc", "file", null, json.toString());
                    System.out.println(json);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 导数据
     */
    @Test
    public void test7() {
        Resource resource = new ClassPathResource("files");
        ObjectMapper objMapper = new ObjectMapper();
        try {
            File fileDir = resource.getFile();
            System.out.println(fileDir.isDirectory());
            ArrayList<String> fileList = new ArrayList<>();
            if (fileDir.exists() && fileDir.isDirectory()) {
                File[] allFiles = fileDir.listFiles();
                for (File f : allFiles) {
                    UserDoc userDoc = tikaService.parserExtraction(f);
                    String json = objMapper.writeValueAsString(userDoc);
                    fileList.add(json);
                }
            }
            restService.indexDoc("userdoc", "file", fileList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test8() {
        String[] searchields = {"title", "filecontent"};

        System.out.println(
        restService.searchDocs("userdoc","中国科学院", searchields,1,10)
        );
    }
}
