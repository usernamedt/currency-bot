package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.WebService;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class CurrencyBot {
    private final static String HELP_MESSAGE = "Привет, это edu.urgu.oopteam.CurrencyBot!" +
            "\nЯ могу показывать курсы валют. Используй команды ниже:" +
            "\n/help - показать это сообщение и список возможных команд" +
            "\n/curr {код валюты} - показать курс указанной валюты к рублю";
    private final static String JSON_PAGE_ADDRESS = "https://www.cbr-xml-daily.ru/daily_json.js";
    private final static Logger LOGGER = Logger.getLogger(CurrencyBot.class.getCanonicalName());
    private IMessenger messenger;
    private CurrenciesJsonModel currModel;
    private ExecutorService pool;

    public CurrencyBot(IMessenger messenger){
        this.messenger = messenger;

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

        pool = Executors.newFixedThreadPool(200);
    }

    private void processMessage(Long chatID, String userMessage) {
        if ("/help".equals(userMessage) || "/start".equals(userMessage)) {
            messenger.sendMessage(chatID, HELP_MESSAGE);
        }
        else if (userMessage.startsWith("/curr ")){
            var message = userMessage.split(" ", 2);
            if (message.length != 2) {
                messenger.sendMessage(chatID, "У данной команды должен быть 1 параметр - название валюты");
                return;
            }
            var exRate = currModel.getExchangeRate(message[1]);
            if (exRate != null){
                messenger.sendMessage(chatID,exRate + " RUB");
            }
            else messenger.sendMessage(chatID, "Я не знаю такой валюты, проверьте наличие такой валюты в списке поддерживаемых");
        }
        else {
            messenger.sendMessage(chatID, "Я Вас не понимаю, проверьте соответствие команды одной из перечисленных в /help");
        }
    }

    public void processMessageAsync(Long chatID, String userMessage){
        CompletableFuture.runAsync(() -> processMessage(chatID, userMessage), pool);
    }

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
}
