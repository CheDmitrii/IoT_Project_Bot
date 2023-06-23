package com.dmitrii.lockbot.Controllers.State;

import com.dmitrii.lockbot.Controllers.Mqtt.HandlerTopicMessage;

public class ThreadPassword implements Runnable{
    private HandlerTopicMessage handler;
    private Boolean isPassswordOpen;
    private Boolean isPassswordClose;
    private Boolean isTub;
    private Boolean isWaitMe;
    private Boolean isWaitHe;
    ThreadPassword(HandlerTopicMessage handler){
        this.handler = handler;
    }
    @Override
    public void run() {
        while (true) {
            if (isWaitMe){
                try {
                    wait();
                }catch (Exception E){
                    throw new RuntimeException();
                }
            }
            if (!isTub) {
                String botVal = handler.getBotValue(), lockVal = handler.getLockValue();
                if (botVal.equals("0") && lockVal.equals("1")) {
                    isPassswordOpen = true;
                    isPassswordClose = false;
                }
                if (botVal.equals("1") && lockVal.equals("0")) {
                    isPassswordOpen = false;
                    isPassswordClose = true;
                }
                if (botVal.equals(lockVal)) {
                    isPassswordOpen = false;
                    isPassswordClose = false;
                }
            }
            notifyAll();
        }
    }
}
