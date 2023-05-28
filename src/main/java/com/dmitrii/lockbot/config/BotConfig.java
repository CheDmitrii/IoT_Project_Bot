package com.dmitrii.lockbot.config;

import com.dmitrii.lockbot.Service.Bot;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

@Configuration
@PropertySource("classpath:/bot.properties")
public class BotConfig {
    @Value ("${bot.name}")
    private String name;

    @Value("${bot.token}")
    private String token;

    @Bean
    public Bot bot(){
        return new Bot(name, token);
    }

}
