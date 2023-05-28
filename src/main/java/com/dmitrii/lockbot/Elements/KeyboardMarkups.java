package com.dmitrii.lockbot.Elements;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class KeyboardMarkups {

    public static InlineKeyboardMarkup getClearMenu(){
        return InlineKeyboardMarkup.builder().build();
    }
    public static InlineKeyboardMarkup getStartMenu(){
        var buttonChageLock = InlineKeyboardButton.builder()
                .text("EditLock")
                .callbackData("editLock")
                .build();
        var buttonChangePasword = InlineKeyboardButton.builder()
                .text("ChangePassword")
                .callbackData("changePassword")
                .build();
        var buttonExitStartMenu = InlineKeyboardButton.builder()
                .text("Exit")
                .callbackData("exitStart")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(buttonChangePasword, buttonChageLock))
                .keyboardRow(List.of(buttonExitStartMenu))
                .build();
    }
     public static InlineKeyboardMarkup getStateLockMenu(){
        var buttonOpen = InlineKeyboardButton.builder()
                .text("OpenLock")
                .callbackData("open")
                .build();
        var buttonClose = InlineKeyboardButton.builder()
                .text("CloseLock")
                .callbackData("close")
                .build();
        var buttonBack = InlineKeyboardButton.builder()
                .text("Back")
                .callbackData("backLock")
                .build();
        var buttonExit = InlineKeyboardButton.builder()
                .text("Exit")
                .callbackData("exitLock")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(buttonClose, buttonOpen))
                .keyboardRow(List.of(buttonBack, buttonExit))
                .build();
     }

     public static InlineKeyboardMarkup getPasswordMenu(){
        var buttonBack = InlineKeyboardButton.builder()
                .text("Back")
                .callbackData("backPassword")
                .build();
        var buttonExit = InlineKeyboardButton.builder()
                .text("Exit")
                .callbackData("exitPassword")
                .build();

        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(buttonBack, buttonExit))
                .build();
     }
}
