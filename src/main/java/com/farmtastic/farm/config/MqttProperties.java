package com.farmtastic.farm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
// @Component
@ConfigurationProperties(prefix = "spring.mqtt") // "mqtt"로 시작하는 설정을 매핑
public class MqttProperties {
    private String url;
    private String clientId;
    // private String username;
    // private String password;
}
