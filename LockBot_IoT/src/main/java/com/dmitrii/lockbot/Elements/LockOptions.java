package com.dmitrii.lockbot.Elements;

import ch.qos.logback.core.joran.conditional.IfAction;
import com.dmitrii.lockbot.Controllers.Mqtt.HandlerTopicMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


//@Component
public class LockOptions {

//    @Autowired
//    @Qualifier("handlerMessage")
//    private HandlerTopicMessage handler;
    private String topicBotValue;
    private String topicLockValue;
    private String topicPasswordValue;
    private boolean isOpen;

//    public void fillField(){
//        this.topicBotValue = handler.getBotValue();
//        this.topicLockValue = handler.getLockValue();
//        this.topicPasswordValue = handler.getPaswordValue();
//        this.isOpen = topicBotValue == "1" ? true : false;
//    }

    public void fillField(HandlerTopicMessage handler){
        this.topicBotValue = handler.getBotValue();
        this.topicLockValue = handler.getLockValue();
        this.topicPasswordValue = handler.getPaswordValue();
        this.isOpen = topicBotValue == "1" ? true : false;
    }

    public String getTopicBotValue() {
        return topicBotValue;
    }

    public String getTopicLockValue() {
        return topicLockValue;
    }

    public String getTopicPasswordValue() {
        return topicPasswordValue;
    }

    public boolean isOpen() {
        return isOpen;
    }

}