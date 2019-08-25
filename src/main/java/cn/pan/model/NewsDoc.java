package cn.pan.model;

public class NewsDoc {
    private int news_id;
    private String news_type;
    private String news_title;
    private String news_fulltext;

    public int getNews_id() {
        return news_id;
    }

    public void setNews_id(int news_id) {
        this.news_id = news_id;
    }

    public String getNews_type() {
        return news_type;
    }

    public void setNews_type(String news_type) {
        this.news_type = news_type;
    }

    public String getNews_title() {
        return news_title;
    }

    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }

    public String getNews_fulltext() {
        return news_fulltext;
    }

    public void setNews_fulltext(String news_fulltext) {
        this.news_fulltext = news_fulltext;
    }

    @Override
    public String toString() {
        return "NewsDoc{" +
                "news_id=" + news_id +
                ", news_type='" + news_type + '\'' +
                ", news_title='" + news_title + '\'' +
                ", news_fulltext='" + news_fulltext + '\'' +
                '}';
    }
}
