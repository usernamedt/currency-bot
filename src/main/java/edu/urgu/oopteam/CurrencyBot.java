package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.ConfigurationSettings;
import edu.urgu.oopteam.services.WebService;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class CurrencyBot extends TelegramLongPollingBot {
    private final static String HELP_MESSAGE = "Привет, это CurrencyBot!" +
            "\nЯ могу показывать курсы валют. Используй команды ниже:" +
            "\n/help - показать это сообщение и список возможных команд" +
            "\n/curr {код валюты} - показать курс указанной валюты к рублю";
    private final static String JSON_PAGE_ADDRESS = "https://www.cbr-xml-daily.ru/daily_json.js";
    private ConfigurationSettings configSettings;
    private CurrenciesJsonModel currModel;
    private final static Logger LOGGER = Logger.getLogger(CurrencyBot.class.getCanonicalName());

    public CurrencyBot(ConfigurationSettings settings, DefaultBotOptions botOptions) {
        super(botOptions);
        configSettings = settings;

        var jsonUpdateTimer = new Timer();
        var jsonUpdateTask = new TimerTask() {
          @Override
          public void run(){
              tryUpdateJsonModel();
              /* Потом тут в случае неудачи нужно будет отправить сообщение всем пользователям бота,
               что обновить данные не удалось, и они получат несколько устаревшие данные.
                Для реализации этого нужно хранить где-то(например в json) все id пользователей, которые уже
                используют бота */
          }
        };
        jsonUpdateTimer.scheduleAtFixedRate(jsonUpdateTask, new Date(), 1000 * 60 * 60 * 4);
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

                var exRate = currModel.getExchangeRate(message[1]);
                if (exRate != null){
                    sendMessage(chatID,exRate + " RUB");
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
            LOGGER.info("Error while sending message to client");
            e.printStackTrace();
        }
    }

    /**
     * Если возможно, обновляет поле currModel.
     * @return Возвращает true или false, в зависимости от того, удалось ли удачно обновить модель
     */
    private boolean tryUpdateJsonModel() {
        var mapper = new ObjectMapper();
        try {
            var webPage = WebService.getPageContent(JSON_PAGE_ADDRESS, "UTF-8");
            currModel = mapper.readValue(webPage, CurrenciesJsonModel.class);
            return true;
        } catch (JsonProcessingException jException){
            LOGGER.info("Error while building CurrenciesJsonModel instance");
            jException.printStackTrace();
        } catch (Exception e){
            LOGGER.info("Error while downloading page");
            e.printStackTrace();
        }
        return false;
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