package cn.pan.dao;

import cn.pan.model.NewsDoc;
import cn.pan.model.NewsMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

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
}
