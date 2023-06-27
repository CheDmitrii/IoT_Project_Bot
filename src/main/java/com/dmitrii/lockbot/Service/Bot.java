package com.dmitrii.lockbot.Service;

import com.dmitrii.lockbot.Controllers.Mqtt.HandlerTopicMessage;
import com.dmitrii.lockbot.Controllers.Mqtt.MqttPublisher;
import com.dmitrii.lockbot.Elements.History.HistoryList;
import com.dmitrii.lockbot.Elements.History.HistoryUnit;
import com.dmitrii.lockbot.Elements.KeyboardMarkups;
import com.dmitrii.lockbot.Elements.LockOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Bot extends TelegramLongPollingBot {
    private final String name;
    private final String token;
    private Long chatId;

    @Value("${topic.bot}")
    private String botTopic;

    @Value("${topic.lock}")
    private String lockTopic;

    @Value("${topic.password}")
    private String passwordTopic;

    @Value("${mqtt.qos}")
    private int qos;

    @Value("${mqtt.retained}")
    private boolean retained;

    @Autowired
    @Qualifier("handlerMessage")
    private HandlerTopicMessage handler;

    @Autowired
    @Qualifier("lockOptions")
    private LockOptions options;

    @Autowired
    @Qualifier("mqttPublisher")
    private MqttPublisher publisher;

    private volatile boolean isPasswordChange = false;
    private volatile boolean isPassswordOpen = false;
    private volatile boolean isPassswordClose = false;
    private volatile boolean isOpen = false;
    private volatile boolean isTub = false;


    public Bot(String name, String token) {
        super();
        this.name = name;
        this.token = token;
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (isPasswordChange){
            if (update.hasMessage() && update.getMessage().hasText()){
                getPassword(update.getMessage().getChatId(), update.getMessage().getText());
                return;
            }
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();
            if (msg.isCommand()) {
                switch (msg.getText()) {
                    case "/start":
                        Thread threadNotification = new Thread(() -> {
                            boolean isWriteOpen = false, isWriteClose = false;
                            Long chatId = msg.getChatId();
                            while (true) {
                                while (isTub)
                                if (handler.getBotValue().equals("0") && handler.getLockValue().equals("1")) {
                                    isPassswordOpen = true;
                                    isPassswordClose = false;
                                }
                                if (handler.getBotValue().equals("1") && handler.getLockValue().equals("0")) {
                                    isPassswordOpen = false;
                                    isPassswordClose = true;
                                }
                                if (handler.getBotValue().equals(handler.getLockValue())) {
                                    isPassswordOpen = false;
                                    isPassswordClose = false;
                                }
                                if (this.isPassswordOpen && !this.isPassswordClose) {
                                    if (!isWriteOpen) {
                                        try {
                                            publisher.publish(botTopic, "1", qos, retained);
                                            isOpen = true;
                                        } catch (Exception e) {
                                            throw new RuntimeException();
                                        }
                                        sendText(chatId, "lock is opened by password");
                                        HistoryList.addHistory(3);
                                        isOpen = true;
                                        isWriteOpen = true;
                                    }
                                } else {
                                    isWriteOpen = false;
                                }
                                if (!this.isPassswordOpen && this.isPassswordClose) {
                                    if (!isWriteClose) {
                                        try {
                                            publisher.publish(botTopic, "0", qos, retained);
                                            isOpen = false;
                                        } catch (Exception e) {
                                            throw new RuntimeException();
                                        }
                                        sendText(chatId, "lock is closed by password");
                                        HistoryList.addHistory(4);
                                        isOpen = false;
                                        isWriteClose = true;
                                    }
                                } else {
                                    isWriteClose = false;
                                }
                            }
                        });

                        threadNotification.start();
                        break;
                    case "/menu":
                        sendMenu(id, "<b>Menu</b>", KeyboardMarkups.getStartMenu());
                        break;
                    case"/state":
                        if (isOpen) sendText(id, "State of lock is <strong>OPEN</strong>");
                        else sendText(id, "State of lock is <strong>CLOSE</strong>");
                        break;
                    case "/history":
                        sendText(id, HistoryList.getHistory());
                        break;
                    default:
                        break;
                }
                return;
            }
        }
        if (update.hasCallbackQuery()){
            var idq = update.getCallbackQuery().getFrom().getId();
            buttonTab(idq,
                    update.getCallbackQuery().getId(),
                    update.getCallbackQuery().getData(),
                    update.getCallbackQuery().getMessage().getMessageId());
        }

    }


    public void sendText(Long who, String what){
        SendMessage message = SendMessage.builder()
                .chatId(who.toString()).parseMode("HTML")
                .text(what).build();
        try {
            execute(message);
        }catch (TelegramApiException e){
            throw new RuntimeException();
        }
    }

    private void getPassword(Long who, String digit){
        if (digit.length() == 4){
            for(int i = 0;i < digit.length(); i++){
                if (!Character.isDigit(digit.charAt(i)) ||
                        Character.getNumericValue(digit.charAt(i)) < 1 ||
                        Character.getNumericValue(digit.charAt(i)) > 4){
                    sendText(who, "No valible password - character \n<strong>Try again</strong>");
                    return;
                }
            }
            try {
                publisher.publish(passwordTopic, digit, qos, retained);
                sendText(who, "password successfuly change");
                this.isPasswordChange = false;
            }catch (Exception e){
                sendText(who, "password not changed try again");
                throw new RuntimeException();
            }
        }else {
            sendText(who, "Length of password is to long \n<strong>Try again</strong>");
        }
    }

    private void sendMenu(Long who, String txt, InlineKeyboardMarkup keyboard){
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(txt)
                .replyMarkup(keyboard)
                .build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void buttonTab(Long id, String queryID, String data, int msgID){
        EditMessageText newTxt = EditMessageText.builder()
                .chatId(id.toString()).parseMode("HTML")
                .messageId(msgID).text("").build();


        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(id.toString()).messageId(msgID).build();

        //Don't understand for what close
        AnswerCallbackQuery close = AnswerCallbackQuery.builder()
                .callbackQueryId(queryID).build();

        switch (data.toString()){
            case "open":
                isTub = true;
                try {
                    publisher.publish(lockTopic, "1", qos, retained);
                    publisher.publish(botTopic, "1", qos, retained);
                    isOpen = true;
                    Thread.sleep(1000);
                }catch (Exception e){
                    throw new RuntimeException();
                }
                newTxt.setText(isOpen == true ? "<em>State of Lock => OPEN</em>":"<em>State of Lock => CLOSE</em>" );
                newKb.setReplyMarkup(KeyboardMarkups.getStateEditMenu());
                HistoryList.addHistory(1);
//                options.fillField(handler);//???????
                isTub = false;
                break;
            case "close":
                isTub = true;
                try{
                    publisher.publish(lockTopic, "0", qos, retained);
                    publisher.publish(botTopic, "0", qos, retained);
                    isOpen = false;
                    Thread.sleep(1000);
                }catch (Exception e){
                    throw new RuntimeException();
                }
                newTxt.setText(isOpen == true ? "<em>State of Lock => OPEN</em>":"<em>State of Lock => CLOSE</em>" );
                newKb.setReplyMarkup(KeyboardMarkups.getStateEditMenu());
                HistoryList.addHistory(2);
                isTub = false;
//                options.fillField(handler);//????
                break;
            case "editLock":
                newTxt.setText(isOpen == true ? "<em>State of Lock => OPEN</em>":"<em>State of Lock => CLOSE</em>" );
                newKb.setReplyMarkup(KeyboardMarkups.getStateEditMenu());
                break;
            case "changePassword":
                this.isPasswordChange = true;
                newTxt.setText("<strong>to change password write <u>four-digit number</u>\n" +
                        "from <u>1, 2, 3 or 4</u> digit</strong>");
                try{
                    execute(newTxt);
                }catch (TelegramApiException e){
                    throw new RuntimeException();
                }
                return;
            case "backEditorLock":
            case "backPassword":
                newTxt.setText("<b>Menu</b>");
                newKb.setReplyMarkup(KeyboardMarkups.getStartMenu());
                break;
            case "exitPassword":
            case "exitEditorLock":
            case "exitStart":
                newTxt.setText("You sucsesfuly exit from MENU\nto BACK in menu write command \\menu again");
                try{
                    execute(newTxt);
                }catch (TelegramApiException e){
                    throw new RuntimeException();
                }
                return;
            default:
                newTxt.setText("EERROORR");
                try{
                    execute(newTxt);
                }catch (TelegramApiException e){
                    throw  new RuntimeException();
                }
                return;
        }
        try {
//            execute(close);
            execute(newTxt);
            execute(newKb);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
}