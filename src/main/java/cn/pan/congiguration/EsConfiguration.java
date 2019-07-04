package cn.pan.congiguration;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Setter
@Getter
@ToString
@ConfigurationProperties(prefix = "elastic.search")
public class EsConfiguration {

    private String host;
    private int port;
    private String clusterName;
    private String schema;
}