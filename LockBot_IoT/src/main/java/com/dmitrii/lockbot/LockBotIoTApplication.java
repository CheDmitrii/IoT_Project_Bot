package com.dmitrii.lockbot;

import com.dmitrii.lockbot.Service.Bot;
import com.dmitrii.lockbot.config.BotConfig;
import com.dmitrii.lockbot.config.MqttConfig;
import com.dmitrii.lockbot.config.OptionsConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@SpringBootApplication
@Import({BotConfig.class, MqttConfig.class, OptionsConfig.class})
//@ComponentScan({"com.dmitrii.lockbot.Service"})
public class LockBotIoTApplication {
    private BotConfig botConfig;
    public static void main(String[] args) throws TelegramApiException {
        SpringApplication.run(LockBotIoTApplication.class, args);
    }
}
