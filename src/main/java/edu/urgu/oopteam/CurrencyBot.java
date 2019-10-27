package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.CurrencyTrackService;
import edu.urgu.oopteam.services.ICurrencyTrackService;
import edu.urgu.oopteam.services.WebService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public class CurrencyBot {
    private final static String HELP_MESSAGE = "Привет, это edu.urgu.oopteam.CurrencyBot!" +
            "\nЯ могу показывать курсы валют. Используй команды ниже:" +
            "\n/help - показать это сообщение и список возможных команд" +
            "\n/curr {код валюты} - показать курс указанной валюты к рублю" +
            "\n/track {код валюты} {дельта} - отслеживать курс указанной валюты и уведомлять при отклонении больше дельты" +
            "\n/allTracked - вывести все текущие отслеживаемые валюты";
    private final static String JSON_PAGE_ADDRESS = "https://www.cbr-xml-daily.ru/daily_json.js";
    private final static Logger LOGGER = Logger.getLogger(CurrencyBot.class.getCanonicalName());
    private CurrenciesJsonModel currModel;
    @Autowired
    private ICurrencyTrackService currencyTrackService;

    public CurrencyBot(ApplicationContext context) {

        var jsonUpdateTimer = new Timer();
        var jsonUpdateTask = new TimerTask() {
            @Override
            public void run() {
                tryUpdateJsonModel();
              /* Потом тут в случае неудачи нужно будет отправить сообщение всем пользователям бота,
               что обновить данные не удалось, и они получат несколько устаревшие данные.
                Для реализации этого нужно хранить где-то(например в json) все id пользователей, которые уже
                используют бота */
            }
        };
        jsonUpdateTimer.scheduleAtFixedRate(jsonUpdateTask, new Date(), 1000 * 60 * 60 * 4);
        currencyTrackService = context.getBean(CurrencyTrackService.class);
    }

    public String processMessage(String userMessage, long chatId) {
        if ("/help".equals(userMessage) || "/start".equals(userMessage)) {
            return HELP_MESSAGE;
        } else if (userMessage.startsWith("/curr ")) {
            var message = userMessage.split(" ", 2);
            if (message.length != 2) {
                return "У данной команды должен быть 1 параметр - название валюты";
            }
            try {
                double exRate = currModel.getExchangeRate(message[1]);
                return exRate + " RUB";
            } catch (NotFoundException e) {
                return "Я не знаю такой валюты, проверьте наличие такой валюты в списке поддерживаемых";
            }
        } else if (userMessage.startsWith("/track ")) {
            var args = userMessage.split(" ", 3);
            if (args.length != 3) {
                return "У данной команды должно быть 2 параметра - код валюты и дельта";
            }
            var currencyCode = args[1].toLowerCase();
            var delta = Double.parseDouble(args[2]);
            double currExchangeRate;
            try {
                currExchangeRate = currModel.getExchangeRate(currencyCode);
            } catch (NotFoundException e) {
                return e.getMessage();
            }

            var trackedCurrency = currencyTrackService.findTrackedCurrency(chatId, currencyCode);
            if (trackedCurrency != null) {
                currencyTrackService.updateTrackedCurrency(trackedCurrency, delta, currExchangeRate);
                return "Обновил существующий запрос \n" + trackedCurrency.toString();
            }
            var trackRequest = currencyTrackService.addTrackedCurrency(chatId, currExchangeRate, currencyCode, delta);
            return "Создал новый запрос... \n" + trackRequest.toString();
        } else if (userMessage.equals("/allTracked")) {
            var userRequests = currencyTrackService.findAllByChatId(chatId);
            return "Ваши текущие запросы на отслеживание:\n" + userRequests.toString();
        } else {
            return "Я Вас не понимаю, проверьте соответствие команды одной из перечисленных в /help";
        }
    }

    private boolean tryUpdateJsonModel() {
        var mapper = new ObjectMapper();
        try {
            var webPage = WebService.getPageContent(JSON_PAGE_ADDRESS, "UTF-8");
            currModel = mapper.readValue(webPage, CurrenciesJsonModel.class);
            return true;
        } catch (JsonProcessingException jException) {
            LOGGER.info("Error while building CurrenciesJsonModel instance");
            jException.printStackTrace();
        } catch (Exception e) {
            LOGGER.info("Error while downloading page");
            e.printStackTrace();
        }
        return false;
    }
}
