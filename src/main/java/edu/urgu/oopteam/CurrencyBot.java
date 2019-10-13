package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.models.CurrencyData;
import edu.urgu.oopteam.services.ConfigurationSettings;
import edu.urgu.oopteam.services.FileService;
import edu.urgu.oopteam.services.WebService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class CurrencyBot extends TelegramLongPollingBot {
    private final static String HELP_MESSAGE = "Привет, это CurrencyBot!" +
            "\nЯ могу показывать курсы валют. Используй команды ниже:" +
            "\n/help - показать это сообщение и список возможных команд" +
            "\n/curr {код валюты} - показать курс указанной валюты к рублю";
    private ConfigurationSettings configSettings;
    private CurrenciesJsonModel currModel;
    private HashMap<String, CurrencyData> currNameToCurrencyData;

    public CurrencyBot(ConfigurationSettings settings){
        super();
        configSettings = settings;
    }
    public CurrencyBot(ConfigurationSettings settings, DefaultBotOptions botOptions) throws Exception {
        super(botOptions);
        configSettings = settings;

        var mapper = new ObjectMapper();
        var fileService = new FileService();
        var file = new File(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "CurrenciesData.json");
        if (file.exists()){
            currModel = mapper.readValue(fileService.readResourceFile("CurrenciesData.json"), CurrenciesJsonModel.class);
        }
        else{
            var webPage = WebService.getPageContent("https://www.cbr-xml-daily.ru/daily_json.js", "UTF-8");
            FileService.saveFile("CurrenciesData", "src" + File.separator + "main" + File.separator + "resources","",".json");
            currModel = mapper.readValue(fileService.readResourceFile("CurrenciesData.json"), CurrenciesJsonModel.class);
        }
        currNameToCurrencyData = getNameToCurrencyDataDict(currModel);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var userMessage = update.getMessage().getText();
            var chatID = update.getMessage().getChatId();

            if ("/help".equals(userMessage) || "/start".equals(userMessage)) {
                sendMessage(chatID, HELP_MESSAGE);
            }
            else if (userMessage.startsWith("/curr ")){
                var message = userMessage.split(" ", 2);
                if (message.length != 2) {
                    sendMessage(chatID, "У данной команды должен быть 1 параметр - название валюты");
                    return;
                }

                /* Позже тут должна быть проверка на то, что пора обновить json файл */
                if (currNameToCurrencyData.containsKey(message[1])){
                    sendMessage(chatID, currNameToCurrencyData.get(message[1]).Value + " " + "RUB");
                }
                else sendMessage(chatID, "Я не знаю такой валюты, проверьте наличие такой валюты в списке поддерживаемых");
            }
            else {
                sendMessage(chatID, "Я Вас не понимаю, проверьте соответствие команды одной из перечисленных в /help");
            }
        }
    }

    private void sendMessage(Long chatID, String message){
        var reply = new SendMessage().setChatId(chatID).setText(message);
        try {
            execute(reply);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return configSettings.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return configSettings.getBotToken();
    }

    private static CurrenciesJsonModel getCurrModelFromJson(String content) {
        var mapper = new ObjectMapper();
        try {
            return mapper.readValue(content, CurrenciesJsonModel.class);
        } catch (JsonProcessingException e) {
            System.out.println("Error while parsing json file");
            e.printStackTrace();
//            System.exit(-1);
        }
        return null;
    }

    private static HashMap<String, CurrencyData> getNameToCurrencyDataDict(CurrenciesJsonModel model) {
        var resultDict = new HashMap<String, CurrencyData>();
        for(var currencyCode : model.Valute.keySet()) {
            resultDict.put(model.Valute.get(currencyCode).Name, model.Valute.get(currencyCode));
        }
        return resultDict;
    }
}