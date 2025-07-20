package com.farmtastic.farm.config;

import com.farmtastic.farm.domain.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MqttCommandPublisher {

    private final MessageChannel mqttOutboundChannel   ;

    //actoator와 명령어를 받아 mqtt 토픽으로 전송하는 메소드
    public void sendCommand(Device actoutor, String command){
        String topic = "farm/control/zone-A/"
                + actoutor.getDeviceName().toLowerCase();

        Message<String> message = MessageBuilder.withPayload(command)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .build();
        mqttOutboundChannel.send(message);
    }
}
