package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.CurrencyTrackService;
import edu.urgu.oopteam.services.ICurrencyTrackService;
import edu.urgu.oopteam.services.WebService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.log4j.Logger;

public class CurrencyBot {
    private final static String HELP_MESSAGE = "Привет, это Currency Bot!" +
            "\nЯ могу показывать курсы валют. Используй команды ниже:" +
            "\n/help - показать это сообщение и список возможных команд" +
            "\n/curr {код валюты} - показать курс указанной валюты к рублю" +
            "\n/track {код валюты} {дельта} - отслеживать курс указанной валюты и уведомлять при отклонении больше дельты" +
            "\n/untrack {код валюты} - перестать отслеживать указанную валюту" +
            "\n/allTracked - вывести все текущие отслеживаемые валюты";
    private final static String JSON_PAGE_ADDRESS = "https://www.cbr-xml-daily.ru/daily_json.js";
//    private final static Logger LOGGER = Logger.getLogger(CurrencyBot.class.getCanonicalName());
    private static final Logger LOGGER = Logger.getLogger(CurrencyBot.class);
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
                Для реализации этого нужно хранить где-то(в БД) все id пользователей, которые уже
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
            return handleCurrCommand(userMessage);

        } else if (userMessage.startsWith("/track ")) {
            return handleTrackCommand(chatId, userMessage);

        } else if (userMessage.startsWith("/untrack ")) {
            return handleUntrackCommand(chatId, userMessage);

        } else if (userMessage.equals("/allTracked")) {
            var userRequests = currencyTrackService.findAllByChatId(chatId);
            return "Ваши текущие запросы на отслеживание:\n" + userRequests.toString();
        } else {
            return "Я Вас не понимаю, проверьте соответствие команды одной из перечисленных в /help";
        }
    }

    private String handleCurrCommand(String userMessage) {
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
    }

    private String handleTrackCommand(long chatId, String userMessage) {
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

        try {
            var trackedCurrency = currencyTrackService.findTrackedCurrency(chatId, currencyCode);
            if (trackedCurrency != null) {
                currencyTrackService.updateTrackedCurrency(trackedCurrency, delta, currExchangeRate);
                return "Обновил существующий запрос \n" + trackedCurrency.toString();
            }
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return "Внутренняя ошибка бота, попробуйте использовать эту команду позже";
        }
        var trackRequest = currencyTrackService.addTrackedCurrency(chatId, currExchangeRate, currencyCode, delta);
        return "Создал новый запрос... \n" + trackRequest.toString();
    }

    private String handleUntrackCommand(long chatId, String userMessage) {
        var args = userMessage.split(" ", 2);
        if (args.length != 2) {
            return "У данной команды должен быть 1 параметр - код валюты, которую вы хотите перестать отслеживать";
        }
        var currencyCode = args[1];
        try {
            var trackedCurrency = currencyTrackService.findTrackedCurrency(chatId, currencyCode);
            if (trackedCurrency == null) {
                return "Такая валюта в данный момент не отслеживается. Чтобы посмотреть список отслеживаемых валют, " +
                        "воспольуйтесь командой /allTracked";
            }
            currencyTrackService.deleteTrackedCurrency(trackedCurrency);
            return "Отслеживание по данной записи успешно отменено.\n" + trackedCurrency.toString();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return "Внутренняя ошибка бота, попробуйте использовать эту команду позже";
        }
    }

    private boolean tryUpdateJsonModel() {
        var mapper = new ObjectMapper();
        try {
            var webPage = WebService.getPageContent(JSON_PAGE_ADDRESS, "UTF-8");
            currModel = mapper.readValue(webPage, CurrenciesJsonModel.class);
            return true;
        } catch (JsonProcessingException jException) {
            LOGGER.error("Error while building CurrenciesJsonModel instance", jException);
        } catch (Exception e) {
            LOGGER.error("Error while downloading page", e);
        }
        return false;
    }
}
