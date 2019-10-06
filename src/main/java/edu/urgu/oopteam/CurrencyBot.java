package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.FileService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;


public class CurrencyBot extends TelegramLongPollingBot {
    private final static String helpMessage = "Hello, it's CurrencyBot!" +
            "\nI can show you some exchange rates. Use commands below:" +
            "\n/help - to show this message and view possible commands";

    public CurrencyBot(){
        super();
    }

    public CurrencyBot(DefaultBotOptions botOptions){
        super(botOptions);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var userMessage = update.getMessage().getText();
            var chatID = update.getMessage().getChatId();

            if (userMessage.equals("/help") || userMessage.equals("/start")) {
                sendMessage(chatID, helpMessage);
            }
            else if (userMessage.startsWith("/curr ")){
                var message = userMessage.split(" ");
                if (message.length > 2) {
                    sendMessage(chatID, "У данной команды должен быть только 1 параметр - название валюты");
                    return;
                }
                ObjectMapper mapper = new ObjectMapper();

                var fileService = new FileService();
                try {
                    var data = mapper.readValue(fileService.readResourceFile("CurrenciesData.json"),  CurrenciesJsonModel.class);

                    if (data.Valute.containsKey(message[1]))
                        sendMessage(chatID, data.Valute.get(message[1]).Value);
                    else
                        sendMessage(chatID, "Нет такой валюты. СосатЬ!");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendMessage(Long chatID, String message){
        var reply = new SendMessage().setChatId(chatID).setText(helpMessage);
        try {
            execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "CurrencySuperBot";
    }

    @Override
    public String getBotToken() {
        try {
            var fileService = new FileService();
            return fileService.readResourceFile("token.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static CurrenciesJsonModel getObjFromJson(String content) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(content,  CurrenciesJsonModel.class);
    }
}