package com.dmitrii.lockbot.Service;

import com.dmitrii.lockbot.Controllers.Mqtt.HandlerTopicMessage;
import com.dmitrii.lockbot.Controllers.Mqtt.MqttPublisher;
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

    private volatile boolean isPasssword = false;

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
        if (isPasssword){
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
                        this.isPasssword = true;
                        this.options.fillField(handler);

                        Thread threadNotification = new Thread(() ->{
                            boolean isWrite = false;
                            Long chatId = msg.getChatId();
                            while (true){
                                if (this.isPasssword){
                                    if (!isWrite) {
                                        sendText(chatId, "LOCK OPEN BY PASSWORD");
                                        isWrite = true;
                                    }
                                }else {
                                    isWrite = false;
                                }
                            }
                        });
                        threadNotification.start();

                        Thread threadIsPassword = new Thread(() -> {
                            while (true){
                                if (handler.isOpenByPassword()){
                                    isPasssword = true;
                                }else {
                                    isPasssword = false;
                                }
                            }
                        });
                        threadIsPassword.start();
                        break;
                    case "/menu":
                        sendMenu(id, "<b>Menu</b>", KeyboardMarkups.getStartMenu());
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

    public void openNotification(Long who){
        boolean isWrite = false;
        while (true){
            if (this.isPasssword){
                if (!isWrite) {
                    sendText(who, "LOCK OPEN BY PASSWORD");
                    isWrite = true;
                }
            }else {
                isWrite = false;
            }
        }
    }

    public void sendText(Long who, String what){
        SendMessage message = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(message);
        }catch (TelegramApiException e){
            throw new RuntimeException();
        }
    }

    public void copyMessage(Long who, Integer messageID){
        CopyMessage copy = CopyMessage.builder()
                .chatId(who.toString())
                .messageId(messageID)
                .fromChatId(who.toString())
                .build();
        try{
            execute(copy);
        }catch (TelegramApiException e){
            throw new RuntimeException();
        }
    }

    private void getPassword(Long who, String digit){
        if (digit.length() == 4){
            int i = 0;
            while (i < digit.length()){
                if (!Character.isDigit(digit.charAt(i))){
                    sendText(who, "that's not digit \n<strong>Try again</strong>");
                    return;
                }
                i++;
            }
            try {
                publisher.publish(passwordTopic, digit, qos, retained);
                sendText(who, "password successfuly change");
                this.isPasssword = false;
            }catch (Exception e){
                sendText(who, "password not changed try again");
                throw new RuntimeException();
            }
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
                newTxt.setText("<em>Lock successfuly open</em>");
                newKb.setReplyMarkup(KeyboardMarkups.getStateLockMenu());
                try {
                    System.out.println("test publisher");
                    publisher.publish(botTopic, "1", qos, retained);
                    publisher.publish(lockTopic, "1", qos, retained);
                }catch (Exception e){
                    throw new RuntimeException();
                }
                options.fillField(handler);//???????
                break;
            case "close":
                newTxt.setText("<em>Lock successfuly close</em>");
                newKb.setReplyMarkup(KeyboardMarkups.getStateLockMenu());
                try{
                    publisher.publish(botTopic, "0", qos, retained);
                }catch (Exception e){
                    throw new RuntimeException();
                }
                options.fillField(handler);//????
                break;
            case "editLock":
                newTxt.setText("<em>Choose state of Lock</em>");
                newKb.setReplyMarkup(KeyboardMarkups.getStateLockMenu());
                break;
            case "changePassword":
                this.isPasssword = true;
                newTxt.setText("<strong>to change password write <u>four-digit number</u>\n" +
                        "from <u>1, 2, 3 or 4</u> digit</strong>");
                try{
                    execute(newTxt);
                }catch (TelegramApiException e){
                    throw new RuntimeException();
                }
                return;
            case "backLock":
            case "backPassword":
                newTxt.setText("<b>Menu</b>");
                newKb.setReplyMarkup(KeyboardMarkups.getStartMenu());
                break;
            case "exitPassword":
            case "exitLock":
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