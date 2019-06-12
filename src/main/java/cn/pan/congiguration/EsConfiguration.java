package cn.pan.congiguration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "elastic.search")
@Setter
@Getter
@ToString
public class EsConfiguration {

    private String host;
    private int port;
    private String clusterName;
}