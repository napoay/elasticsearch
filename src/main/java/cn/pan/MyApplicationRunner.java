package cn.pan;

import cn.pan.controller.IndexController;
import cn.pan.dao.NewsJDBC;
import cn.pan.model.NewsDoc;
import cn.pan.model.UserDoc;
import cn.pan.service.EsRestService;
import cn.pan.service.TikaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.apache.cxf.transport.http.auth.HttpAuthHeader;
import org.apache.log4j.Logger;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class MyApplicationRunner implements ApplicationRunner {
    private static Logger logger = Logger.getLogger(MyApplicationRunner.class.getClass());

    @Autowired
    EsRestService restService;

//    @Autowired
//    TikaService tikaService;

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

                logger.info("init index susscess. index_name: userdoc, type_name: file, 分片数: 3, 副本数: 0");
                /**
                 * 批量导数据
                 */
                Resource resource = new ClassPathResource("files");
                ObjectMapper objMapper = new ObjectMapper();

                File fileDir = resource.getFile();
                ArrayList<String> fileList = new ArrayList<>();
//                if (fileDir.exists() && fileDir.isDirectory()) {
//                    File[] allFiles = fileDir.listFiles();
//                    for (File f : allFiles) {
//                        UserDoc userDoc = tikaService.parserExtraction(f);
//                        System.out.println(userDoc.toString());
//                        String json = objMapper.writeValueAsString(userDoc);
//                        fileList.add(json);
//                    }
//                }
                ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
                NewsJDBC newsJDBC = (NewsJDBC) context.getBean("newsJDBC");

//                for (int id = 5914747; id <= 5915747; id++) {
//                    NewsDoc newsDoc = newsJDBC.getNewsDoc(id);
//                    UserDoc userDoc = new UserDoc();
//                    userDoc.setTitle(newsDoc.getNews_title());
//                    userDoc.setFilecontent(newsDoc.getNews_fulltext());
//                    String json = objMapper.writeValueAsString(userDoc);
//                    fileList.add(json);
//                }

                List<NewsDoc> allDoc = newsJDBC.getAllDoc();
                for (NewsDoc doc:
                     allDoc) {
                    UserDoc userDoc = new UserDoc();
                    userDoc.setTitle(doc.getNews_title());
                    userDoc.setFilecontent(doc.getNews_fulltext());
                    String json = objMapper.writeValueAsString(userDoc);
                    fileList.add(json);
                    System.out.println(doc.getNews_title());
                }
                System.out.println("selected news doc "+ fileList.size());
                restService.indexDoc("userdoc", "file", fileList);

                IndexController.word2int = new HashMap<>();
                IndexController.int2word = new HashMap<>();
                BufferedReader br = new BufferedReader(new FileReader("D:\\Github\\esfilesearch\\src\\main\\resources\\mymodel\\allWords.txt"));
                int count = 0;
                String line = null;
                while ((line = br.readLine())!=null){
                    IndexController.word2int.put(line, count);
                    IndexController.int2word.put(count, line);
                    count ++;
                }



            } else {
                logger.error("索引初始化失败.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
