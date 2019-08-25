package cn.pan.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NewsMapper implements RowMapper<NewsDoc> {
    @Override
    public NewsDoc mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        NewsDoc news = new NewsDoc();
        news.setNews_id(resultSet.getInt("news_id"));
        news.setNews_type(resultSet.getString("news_type"));
        news.setNews_title(resultSet.getString("news_title"));
        news.setNews_fulltext(resultSet.getString("news_fulltext"));
        return news;
    }

}
