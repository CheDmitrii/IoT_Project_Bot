package com.dmitrii.lockbot.config;

import com.dmitrii.lockbot.Controllers.Mqtt.HandlerTopicMessage;

import static com.dmitrii.lockbot.Elements.MqttClients.MqttClientFactory;
import static com.dmitrii.lockbot.Elements.MqttClients.connectOptions;

import com.dmitrii.lockbot.Controllers.Mqtt.MqttPublisher;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.event.MqttConnectionFailedEvent;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * IoTLock/bot - bot write in this topic
 * IoTLock/lock - lock write in this topic
 * IoTLock/password - what password open lock
 * */


@Configuration
@PropertySource("classpath:/MQTT.properties")
public class MqttConfig {

    @Bean
    public MqttClient client(@Value("${mqtt.url}") String url,
                             @Value("${mqtt.tocken}") String tocken) throws  MqttException{
        MqttClient client = new MqttClient(url, MqttAsyncClient.generateClientId());
        client.connect(connectOptions(tocken));
        return client;
    }

    @Bean
    public MessageChannel mqttInputChanel(){
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbount(@Value("${topic.bot}") String topicBot,
                                   @Value("${topic.lock}") String topicLock,
                                   @Value("${topic.password}") String topicPassword,
                                   @Value("${mqtt.qos}") int qos){
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(MqttAsyncClient.generateClientId(),
                MqttClientFactory(), topicLock, topicBot, topicPassword);

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(qos);
        adapter.setOutputChannel(mqttInputChanel());

        return adapter;
    }


//    @Bean
//    public MessageProducer inbountLock(@Value("${topic.lock}") String topicLock,
//                                       @Value("${mqtt.qos}") int qos){
//        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(MqttAsyncClient.generateClientId(),
//                MqttClientFactory(), topicLock);
//
//        adapter.setCompletionTimeout(5000);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(qos);
//        adapter.setOutputChannel(mqttInputChanel());
//
//        return adapter;
//    }
//
//    @Bean
//    public MessageProducer inbountBot(@Value("${topic.bot}") String topicBot,
//                                   @Value("${mqtt.qos}") int qos){
//        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(MqttAsyncClient.generateClientId(),
//                MqttClientFactory(), topicBot);
//
//        adapter.setCompletionTimeout(5000);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(qos);
//        adapter.setOutputChannel(mqttInputChanel());
//
//        return adapter;
//    }
//
//    @Bean
//    public MessageProducer inbountPassword(@Value("${topic.password}") String topicPassword,
//                                      @Value("${mqtt.qos}") int qos){
//        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(MqttAsyncClient.generateClientId(),
//                MqttClientFactory(), topicPassword);
//
//        adapter.setCompletionTimeout(5000);
//        adapter.setConverter(new DefaultPahoMessageConverter());
//        adapter.setQos(qos);
//        adapter.setOutputChannel(mqttInputChanel());
//
//        return adapter;
//    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChanel") //test topic lock ------------
    public HandlerTopicMessage handlerMessage(){ //MessageHandler
        return new HandlerTopicMessage();
    }

//    @Bean
//    @ServiceActivator(inputChannel = "mqttInputChanel")
//    public MessageHandler handlerBot(){
//        return new HandlerBotTopic();
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "mqttInputChanel")
//    public MessageHandler handlerPassword(){
//        return new HandlerPaswordTopic();
//    }


    @Bean
    public MqttPublisher mqttPublisher(){
        return new MqttPublisher();
    }


    @Bean
    public MessageChannel mqttOutbountChanel(){
        return new DirectChannel();
    }


    @Bean
    @ServiceActivator(inputChannel = "mqttOutbountChanel")
    public MessageHandler mqttOutbountBot(@Value("${topic.bot}") String topic){
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(MqttAsyncClient.generateClientId(), MqttClientFactory());

        handler.setAsync(true);
        handler.setDefaultTopic(topic);
        return handler;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutbountChanel")
    public MessageHandler mqttOutbounyLock(@Value("${topic.lock}") String topic){
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(MqttAsyncClient.generateClientId(), MqttClientFactory());

        handler.setAsync(true);
        handler.setDefaultTopic(topic);
        return handler;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutbountChanel")
    public MessageHandler mqttOutbounyPassword(@Value("${topic.password}") String topic){
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(MqttAsyncClient.generateClientId(), MqttClientFactory());

        handler.setAsync(true);
        handler.setDefaultTopic(topic);
        return handler;
    }




    //Get list of more detailed Exeprions
    @Bean
    public ApplicationListener<?> eventListener() {
        return new ApplicationListener<MqttConnectionFailedEvent>() {
            @Override
            public void onApplicationEvent(MqttConnectionFailedEvent event) {
                event.getCause().printStackTrace();
            }

        };
    }
}