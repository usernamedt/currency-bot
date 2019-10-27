package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.ConfigurationSettings;
import edu.urgu.oopteam.services.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class TelegramCurrencyBot extends TelegramLongPollingBot {
    private final static Logger LOGGER = Logger.getLogger(TelegramCurrencyBot.class.getCanonicalName());
    private ConfigurationSettings configSettings;
    private CurrencyBot bot;
    private ExecutorService pool = Executors.newFixedThreadPool(200);

    public TelegramCurrencyBot(ConfigurationSettings settings, DefaultBotOptions botOptions, ApplicationContext context) {
        super(botOptions);
        configSettings = settings;
        bot = new CurrencyBot(context);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var userMessage = update.getMessage().getText();
            var chatID = update.getMessage().getChatId();
            processMessageAsync(chatID, userMessage);
        }
    }

    public void sendMessage(Long chatID, String message){
        var reply = new SendMessage().setChatId(chatID).setText(message);
        try {
            execute(reply);
        } catch (TelegramApiException e) {
            LOGGER.info("Error while sending message to client");
            e.printStackTrace();
        }
    }

    public void processMessageAsync(Long chatID, String userMessage){
        CompletableFuture.runAsync(() -> sendMessage(chatID, bot.processMessage(userMessage, chatID)), pool);
    }

    @Override
    public String getBotUsername() {
        return configSettings.getBotUserName();
    }

    @Override
    public String getBotToken() {
        return configSettings.getBotToken();
    }
}