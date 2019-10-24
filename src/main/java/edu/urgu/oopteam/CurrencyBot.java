package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.WebService;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
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

    public CurrencyBot(){

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

    public String processMessage(String userMessage) {
        if ("/help".equals(userMessage) || "/start".equals(userMessage)) {
            return HELP_MESSAGE;
        }
        else if (userMessage.startsWith("/curr ")){
            var message = userMessage.split(" ", 2);
            if (message.length != 2) {
                return "У данной команды должен быть 1 параметр - название валюты";
            }
            var exRate = currModel.getExchangeRate(message[1]);
            if (exRate != null){
                return exRate + " RUB";
            }
            else return "Я не знаю такой валюты, проверьте наличие такой валюты в списке поддерживаемых";
        }
        else {
            return "Я Вас не понимаю, проверьте соответствие команды одной из перечисленных в /help";
        }
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
