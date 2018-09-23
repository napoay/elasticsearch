package cn.pan.model;

import org.springframework.stereotype.Component;

@Component
public class UserDoc {


    private String title;
    private String filecontent;


    public UserDoc() {
    }

    public UserDoc(String title, String filecontent) {
        this.title = title;
        this.filecontent = filecontent;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setFilecontent(String filecontent) {
        this.filecontent = filecontent;
    }

    public String getTitle() {
        return title;
    }

    public String getFilecontent() {
        return filecontent;
    }


    @Override
    public String toString() {
        return "UserDoc{" +
                "title='" + title + '\'' +
                ", filecontent='" + filecontent + '\'' +
                '}';
    }
}
