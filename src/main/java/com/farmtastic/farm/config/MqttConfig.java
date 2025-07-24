package com.farmtastic.farm.config;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
@EnableConfigurationProperties(MqttProperties.class)
@RequiredArgsConstructor
@Slf4j
public class MqttConfig {

    private final MqttProperties mqttProperties;

    // MQTT 클라이언트 연결 설정
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        // yml 파일에서 읽어온 URL 설정
        options.setServerURIs(new String[]{mqttProperties.getUrl()});
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        // 필요한 경우 username, password 설정 추가
        // options.setUserName(mqttProperties.getUsername());
        // options.setPassword(mqttProperties.getPassword().toCharArray());

        factory.setConnectionOptions(options);
        return factory;
    }

    // 메시지 발행 (Publish) 설정
    // 메시지를 내보낼
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(mqttProperties.getClientId() + "_pub", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("default/topic"); // 기본 토픽 설정
        log.info("mqtt 발신할 messageHandler:{}", messageHandler);
        return messageHandler;
    }

    // 메시지 수신 (Subscribe) 설정
    // 메시지를 받아들일
    @Bean
    public MessageChannel mqttInboundChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer mqttInbound() {
        // C++이 보내는 센서 데이터를 수신할 토픽 (구독할 토픽)
        String topic = "farm/data/#"; //센서 데이터(light, ph, water)
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProperties.getClientId() + "_sub", mqttClientFactory(), topic);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInboundChannel());
        return adapter;
    }
}