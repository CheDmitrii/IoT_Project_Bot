package com.dmitrii.lockbot.config;


import com.dmitrii.lockbot.Controllers.Mqtt.HandlerTopicMessage;
import com.dmitrii.lockbot.Elements.LockOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageHandler;

@Configuration
public class OptionsConfig {

    @Bean
    public LockOptions lockOptions(){
        return new LockOptions();
    }
}
