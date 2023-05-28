package com.dmitrii.lockbot.Controllers.Mqtt;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * Open - state which say open or close lock
 * isOpenPassword - state open by password
 * TopicBot - topic state on which write bot
 * TopicLock - topic state on which write lock
 * */

public class HandlerBotTopic implements MessageHandler {
    private static boolean Open = false;
    private static boolean isOpenPassword;
    private static String TopicBotState = "0";
    private static String TopicLockState = "0";
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();

//        if(topic.equals("IoTLock/lock")){
//            switch (TopicLockState){
//                case "1":
//                    if (Open && TopicBotState.equals("0") ){
//                        System.out.println("Lock open by password: " + message.getPayload());
//                        TopicBotState = "1";
//                        isOpenPassword = true;
//                    }
//                    break;
//                default:
//                    break;
//            }
//        }
        if (topic.equals("IoTLock/bot")){
            System.out.println(topic);
            System.out.println(message.getPayload());
//            switch (TopicBotState){
//                case "0":
//                    if (!Open){
//                        TopicLockState = "0";
//                        System.out.println("Lock close");
//                    }
//                    break;
//                case "1":
//                    if (Open){
//                        System.out.println("Lock open");
//                    }
//                    break;
//                default:
//                    break;
//            }
        }



//        Thread threadBot = new Thread(() ->{
//            if (topic.equals("IoTLock/bot")){
//                if(!message.getPayload().equals(TopicBot)){
//                    TopicBot = message.getPayload().toString();
//                }
//            }
//        });
//        Thread threadLock = new Thread(() -> {
//        });

    }

    public void Open(){
        Open = true;
        TopicBotState = "1";
    }
    public void Close(){
        Open = false;
        TopicBotState = "0";
    }
}
