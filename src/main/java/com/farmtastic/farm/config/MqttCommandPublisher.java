package com.farmtastic.farm.config;

import com.farmtastic.farm.domain.Device;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
// @RequiredArgsConstructor
public class MqttCommandPublisher {

    private final MessageChannel mqttOutboundChannel;

    // 생성자를 직접 작성하고, 파라미터에 @Qualifier 추가
    @Autowired
    public MqttCommandPublisher(@Qualifier("mqttOutboundChannel") MessageChannel mqttOutboundChannel) {
        this.mqttOutboundChannel = mqttOutboundChannel;
    }

    //3. actoator와 명령어를 받아 mqtt 토픽으로 전송하는 메소드
    public void sendCommand(Device actoutor, String command){

        if (actoutor == null || actoutor.getDeviceName()==null){
            throw new IllegalArgumentException("actuator or dvice == null");
        }
        //4. ex) farm/control/zone-A/water_pump or farm/control/zone-A/led로 전달
        String deviceName = actoutor.getDeviceName().toLowerCase().trim();
        String topic = "farm/control/zone-A/" + deviceName;

        Message<String> message = MessageBuilder.withPayload(command)
                .setHeader(MqttHeaders.TOPIC, topic)
                .setHeader(MqttHeaders.QOS, 1)
                .build();
        mqttOutboundChannel.send(message);
    }
}
