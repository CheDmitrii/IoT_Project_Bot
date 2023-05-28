package com.dmitrii.lockbot.Elements;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;


public class MqttClients {

    public static MqttConnectOptions connectOptions(String tocken){
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"ssl://mqtt.flespi.io:8883"});
        options.setUserName(tocken);
        options.setCleanSession(true);
        return options;
    }

    public static MqttPahoClientFactory MqttClientFactory(){ //  DefaultMqttPahoClientFactory
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"ssl://mqtt.flespi.io:8883"});
        options.setUserName("aOKHDLpdeDKOtCFR37k9GtgJGmyci5mhIVO9u1dzvLbOeGw7DE48Y8Hsk3urWFys");
        options.setCleanSession(true);

        factory.setConnectionOptions(options);

        return factory;
    }


}
