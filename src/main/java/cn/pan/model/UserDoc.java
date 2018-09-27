package cn.pan.model;

import lombok.*;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class UserDoc {
    private String title;
    private String filecontent;
}
