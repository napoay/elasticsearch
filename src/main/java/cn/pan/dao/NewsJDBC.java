package cn.pan.dao;

import cn.pan.model.NewsDoc;
import cn.pan.model.NewsMapper;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapperResultSetExtractor;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;


public class NewsJDBC implements cn.pan.dao.newsDAO {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplateObj;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplateObj = new JdbcTemplate(dataSource);
    }

    @Override
    public NewsDoc getNewsDoc(Integer news_id) {
        String SQL = "SELECT * FROM news_data_10000 WHERE news_id = ? ";
        NewsDoc newsDoc = jdbcTemplateObj.queryForObject(SQL, new Object[]{news_id}, new NewsMapper());
        return newsDoc;
    }

    public  List<NewsDoc>  getAllDoc(){
        String SQL = "SELECT * FROM news_data_10000 WHERE news_type LIKE 'world%' ";
        List<NewsDoc> query = jdbcTemplateObj.query(SQL, new NewsMapper());
        return query;
    }

    @Test
    public void getNewsDocTest(){
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        NewsJDBC newsJDBC = (NewsJDBC) context.getBean("newsJDBC");
        NewsDoc newsDoc = newsJDBC.getNewsDoc(5914806);
        System.out.println(newsDoc.toString());
    }

    @Test
    public void getAllDocTest(){
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        NewsJDBC newsJDBC = (NewsJDBC) context.getBean("newsJDBC");
        List<NewsDoc> allDoc = newsJDBC.getAllDoc();
        System.out.println(allDoc.size());
    }

}
