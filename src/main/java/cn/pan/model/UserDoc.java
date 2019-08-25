package cn.pan.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDoc {
    private String title;
    private String news_type;
    private String filecontent;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFilecontent(String filecontent) {
        this.filecontent = filecontent;
    }

    public void setType(String type){
        this.news_type = type;
    }

    @Override
    public String toString() {
        return "UserDoc{" +
                "title='" + title + '\'' +
                ", filecontent='" + filecontent + '\'' +
                '}';
    }
}
