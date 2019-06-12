package cn.pan;

import cn.pan.model.UserDoc;
import cn.pan.service.EsRestService;
import cn.pan.service.TikaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


@Component
public class MyApplicationRunner implements ApplicationRunner {
    private static Logger logger = Logger.getLogger(MyApplicationRunner.class.getClass());


    @Autowired
    EsRestService restService;

    @Autowired
    TikaService tikaService;

    @Override
    public void run(ApplicationArguments var1) throws Exception {

        //删除userdoc索引

        restService.deleteIndex("userdoc");

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

            //初始化索引
            Boolean isSuccess = restService.initIndex("userdoc",
                    "file", 3, 0, builder);

            if (isSuccess) {

                logger.info("索引初始化成功.索引名: userdoc,类型名: file,分片数: 3,副本数: 0");
                /**
                 * 批量导数据
                 */
                Resource resource = new ClassPathResource("files");
                ObjectMapper objMapper = new ObjectMapper();

                File fileDir = resource.getFile();
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

            } else {
                logger.error("索引初始化失败.");

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
