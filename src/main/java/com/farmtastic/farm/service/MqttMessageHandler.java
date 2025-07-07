package com.farmtastic.farm.service;

import com.farmtastic.farm.dto.LogReceiveDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

// MqttConfig에서 설정한 mqttInboundChannel로 들어오는 모든 MQTT 메시지를 이 클래스의 메서드가 받아서 처리
@Slf4j
@Component
@RequiredArgsConstructor
public class MqttMessageHandler {
    private final ObjectMapper objectMapper;
    private final SensorLogService sensorLogService;

    // MqttConfig의 mqttInboundChannel로 들어오는 메시지를 처리하는 리스너.
    @ServiceActivator(inputChannel = "mqttInboundChannel")
    public void handleMessage(Message<String> message) {

        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC, String.class);
        String payload = message.getPayload();

        log.info("MQTT 메시지 수신 - Topic: {}, Payload: {}", topic, payload);

        // 토픽에 따라 다른 처리를 하는 로직
        // 예: "farm/+/sensor/+/data" 토픽으로 온 데이터만 처리
        if (topic != null && topic.endsWith("/data")) {
            try {
                // 받은 JSON 문자열(payload)을 DTO 객체로 변환
                LogReceiveDto logDto = objectMapper.readValue(payload, LogReceiveDto.class);

                sensorLogService.saveSensorLog(logDto);
                log.info("센서 로그가 성공적으로 저장되었습니다. Device ID: {}", logDto.getDeviceId());

            } catch (JsonProcessingException e) {
                log.error("MQTT 메시지 JSON 파싱 오류: {}", payload, e);
            } catch (Exception e) {
                log.error("센서 로그 저장 중 오류 발생: {}", payload, e);
            }
        }
    }
}
