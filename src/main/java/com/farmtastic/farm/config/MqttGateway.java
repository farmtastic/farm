package com.farmtastic.farm.config;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

// 서비스 계층에서 MQTT 메시지를 쉽게 발행할 수 있도록 제공하는 게이트웨이 인터페이스
// 다른 서비스에서 복잡한 채널 설정 없이 mqttGateway.sendToMqtt(topic, payload) 한 줄만으로 메시지를 보낼 수 있음.
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel") // 메시지를 보낼 채널 지정
public interface MqttGateway {

    // 지정된 토픽으로 메시지(payload)를 보냄
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);

}
