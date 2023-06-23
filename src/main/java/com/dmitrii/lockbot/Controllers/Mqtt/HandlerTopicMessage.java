package com.dmitrii.lockbot.Controllers.Mqtt;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

public class HandlerTopicMessage implements MessageHandler {
    private String botValue;
    private String lockValue;
    private String paswordValue;
    private boolean isByPasswordOpen;
    private boolean isByPasswordClose;
    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
        if (topic.equals("IoTLock/lock")){
            lockValue = (String) message.getPayload();
            System.out.println("LOCK TOPIC");
            System.out.println(topic);
            System.out.println(lockValue);
        }
        if (topic.equals("IoTLock/bot")){
            botValue = (String) message.getPayload();
//            if (botValue.equals(lockValue)){
//                isByPasswordOpen = false;
//            }else {
//                isByPasswordOpen = true;
//            }
            System.out.println("BOT TOPIC");
            System.out.println(topic);
            System.out.println(botValue);
        }
        if (topic.equals("IoTLock/password")){
            paswordValue = (String) message.getPayload();
            System.out.println("PASSWORD TOPIC");
            System.out.println(topic);
            System.out.println(paswordValue);
        }
//        if (lockValue.equals("1") && botValue.equals("0")){
//            this.isByPasswordOpen = true;
//            this.isByPasswordClose = false;
//        }
//        if (lockValue.equals("0") && botValue.equals("1")){
//            this.isByPasswordOpen = false;
//            this.isByPasswordClose = true;
//        }
    }

    public String getBotValue() {
        return botValue;
    }

    public String getLockValue() {
        return lockValue;
    }

    public String getPaswordValue() {
        return paswordValue;
    }

    public boolean isByPasswordOpen() {
        return isByPasswordOpen;
    }
}
