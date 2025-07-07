package com.farmtastic.farm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "mqtt") // "mqtt"로 시작하는 설정을 매핑
public class MqttProperties {
    private String url;
    private String clientId;
    // private String username;
    // private String password;
}
