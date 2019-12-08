package edu.urgu.oopteam;

import edu.urgu.oopteam.services.ConfigurationSettings;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;


@Component
public class TelegramCurrencyBot extends TelegramLongPollingBot implements IMessenger {
    private final static Logger LOGGER = Logger.getLogger(TelegramCurrencyBot.class);
    private final ConfigurationSettings configSettings;
    private TelegramUpdateHandler updateHandler;

    @Autowired
    public TelegramCurrencyBot(ConfigurationSettings settings) {
        configSettings = settings;
    }

    @Override
    public void setUpdateHandler(TelegramUpdateHandler handler) {
        updateHandler = handler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var userMessage = update.getMessage().getText();
            var chatID = update.getMessage().getChatId();
            updateHandler.handleUpdate(chatID, userMessage);
        }
    }

    @Override
    public void sendMessage(Long chatID, String message) {
        var reply = new SendMessage().setChatId(chatID).setText(message);
        try {
            execute(reply);
        } catch (TelegramApiException e) {
            LOGGER.error("Error while sending message to client", e);
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

    @Override
    public void run() throws TelegramApiRequestException {
        TelegramBotsApi botsApi = new TelegramBotsApi();
        botsApi.registerBot(this);
    }
}