package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.crud.model.User;
import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.*;
import javassist.NotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CurrencyBot {
    private final static String HELP_MESSAGE = "Hello, it's Currency Bot!" +
            "\r\nI can show you some exchange rates. Use commands below:" +
            "\r\n/help - to show this message and possible commands" +
            "\r\n/curr {currency code} - to show specified currency to RUB exchange rate" +
            "\r\n/track {currency code} {delta} - start to track specified currency rate and notify if it changes more/less than delta" +
            "\r\n/untrack {currency code} - stop to track specified currency" +
            "\r\n/allTracked - show all being tracked currencies" +
            "\r\n/lang {language code} - set language (available languages: ru, en)" +
            "\r\n/exchange {currency code} {city: moskva / ekaterinburg / sankt-peterburg}";
    private final static String UNKNOWN_REQ_MESSAGE = "I don't understand you, check if required command matches one of enlisted in /help message";
    private final static String JSON_PAGE_ADDRESS = "https://www.cbr-xml-daily.ru/daily_json.js";
    private static final Logger LOGGER = Logger.getLogger(CurrencyBot.class);
    CurrenciesJsonModel currModel;
    private IMessenger messenger;
    private ExecutorService pool = Executors.newFixedThreadPool(200);
    private ICurrencyTrackService currencyTrackService;
    private IUserService userService;
    private ITranslationService localizer;
    private ICurrencyCashExchangeService currencyCashExchangeService;
    private WebService webService;

    @Autowired
    public CurrencyBot(ICurrencyTrackService currencyTrackService,
                       IUserService userService,
                       ITranslationService localizer,
                       ICurrencyCashExchangeService currencyCashExchangeService,
                       IMessenger messenger,
                       WebService webService
                       ) {
        this.messenger = messenger;
        messenger.setUpdateHandler(this::processMessageAsync);
        var jsonUpdateTimer = new Timer();
        var jsonUpdateTask = new TimerTask() {
            @Override
            public void run() {
                var success = tryUpdateJsonModel();
                if (success) {
                    notifyTrackedUsers();
                }
            }
        };
        jsonUpdateTimer.scheduleAtFixedRate(jsonUpdateTask, new Date(), 1000 * 60 * 60);
        this.currencyTrackService = currencyTrackService;
        this.userService = userService;
        this.localizer = localizer;
        this.currencyCashExchangeService = currencyCashExchangeService;
        this.webService = webService;
    }

    /**
     * Notifies users which are tracking some currencies if delta is greater than requested
     */
    void notifyTrackedUsers() {
        var requests = currencyTrackService.findAll();
        requests.forEach(request -> {
            try {
                var currentRate = currModel.getExchangeRate(request.getCurrencyCode());
                var currentDelta = currentRate - request.getBaseRate();
                if (request.getDelta() * (currentDelta - request.getDelta()) >= 0) {
                    CompletableFuture.runAsync(() -> {
                        var localizedMessage = localizer.localize(
                                "Rate of your tracked currency has changed",
                                request.getUser().getLanguageCode()) + " " + request.getCurrencyCode();

                        messenger.sendMessage(request.getUser().getChatId(), localizedMessage);
                        currencyTrackService.deleteTrackedCurrency(request);
                    });
                }
            } catch (NotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        });
    }

    private void processMessageAsync(Long chatID, String userMessage) {
        CompletableFuture.runAsync(() -> processMessage(chatID, userMessage), pool);
    }

    /**
     * Parses a command then processes it
     *
     * @param chatId      User's chat ID
     * @param userMessage User's message
     */
    private void processMessage(Long chatId, String userMessage) {
        var user = userService.getFirstByChatId(chatId);
        if (user == null) {
            user = userService.createUser(chatId);
        }

        if ("/help".equals(userMessage) || "/start".equals(userMessage)) {
            messenger.sendMessage(chatId, localizer.localize(HELP_MESSAGE, user.getLanguageCode()));
        } else if (userMessage.startsWith("/curr ")) {
            messenger.sendMessage(chatId, localizer.localize(handleCurrCommand(userMessage), user.getLanguageCode()));
        } else if (userMessage.startsWith("/track ")) {
            messenger.sendMessage(chatId, localizer.localize(handleTrackCommand(userMessage, user), user.getLanguageCode()));
        } else if (userMessage.startsWith("/untrack ")) {
            messenger.sendMessage(chatId, localizer.localize(handleUntrackCommand(userMessage, user), user.getLanguageCode()));
        } else if (userMessage.equals("/allTracked")) {
            var userRequests = currencyTrackService.findAllByUserId(user.getId());
            messenger.sendMessage(chatId, localizer.localize("Your current tracking requests:", user.getLanguageCode()) + "\n" + userRequests.toString());
        } else if (userMessage.startsWith("/lang ")) {
            messenger.sendMessage(chatId, localizer.localize(handleLang(chatId, userMessage), user.getLanguageCode()));
        } else if (userMessage.startsWith("/exchange ")) {
            messenger.sendMessage(chatId, localizer.localize(handleExchange(userMessage, user), user.getLanguageCode()));

        } else {
            messenger.sendMessage(chatId, localizer.localize(UNKNOWN_REQ_MESSAGE, user.getLanguageCode()));
        }
    }

    /**
     * Handles /exchange command
     *
     * @param userMessage User's message
     * @return Reply to a user
     */
    String handleExchange(String userMessage, User user) {
        var message = userMessage.split(" ");
        if (message.length != 3) {
            return UNKNOWN_REQ_MESSAGE;
        }
        try {
            var bestExchangeRate = currencyCashExchangeService.getCashExchangeRate(message[1], message[2]);
            return localizer.localize("Best cash exchange rates:\n", user.getLanguageCode()) +
                    localizer.localize("Buy rates:\n", user.getLanguageCode()) +
                    bestExchangeRate.getBuyBankName() + " - " + bestExchangeRate.getBuyRate() + "\n" +
                    localizer.localize("Sell rates:\n", user.getLanguageCode()) +
                    bestExchangeRate.getSellBankName() + " - " + bestExchangeRate.getSellRate();
        } catch (Exception e) {
            return "Bad luck, sorry, try later lol :)";
        }
    }

    /**
     * Handles /lang command (which changes language to specified)
     *
     * @param chatId      User's chat ID
     * @param userMessage User's message
     * @return Reply to a user
     */
    String handleLang(long chatId, String userMessage) {
        var message = userMessage.split(" ");
        if (message.length != 2) {
            return UNKNOWN_REQ_MESSAGE;
        }
        if (localizer.languageExists(message[1])) {
            userService.setLanguage(chatId, message[1]);
            return "Your language has been successfully changed";
        }
        return "No such language supported";
    }

    /**
     * Handles /curr command (for more info use documentation)
     *
     * @param userMessage User's message
     * @return Reply to a user (exchange rate for a currency)
     */
    String handleCurrCommand(String userMessage) {
        var message = userMessage.split(" ");
        if (message.length != 2) {
            return UNKNOWN_REQ_MESSAGE;
        }
        try {
            double exRate = currModel.getExchangeRate(message[1]);
            return exRate + " RUB";
        } catch (NotFoundException e) {
            return "I don't know this currency, please check supporting currencies";
        }
    }

    /**
     * Handles /track command (for more info use documentation)
     *
     * @param user        User object
     * @param userMessage User's message
     * @return Message for user that tells if everything processed right
     */
    String handleTrackCommand(String userMessage, User user) {
        var args = userMessage.split(" ");
        if (args.length != 3) {
            return "This command should only have 2 parameters: currency code and delta";
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
            var trackedCurrency = currencyTrackService.findTrackedCurrency(user.getId(), currencyCode);
            if (trackedCurrency == null) {
                var trackRequest = currencyTrackService.addTrackedCurrency(currExchangeRate, currencyCode, delta, user);
                return localizer.localize("New request added", user.getLanguageCode()) + "\n" + trackRequest.toString();
            }
            currencyTrackService.updateTrackedCurrency(trackedCurrency, delta, currExchangeRate);
            return localizer.localize("Existing request has been updated", user.getLanguageCode()) + "\n" + trackedCurrency.toString();

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return "Internal bot error, try to use this command later";
        }
    }

    /**
     * Handles /untrack command (for more info use documentation)
     *
     * @param user        User object
     * @param userMessage User's message
     * @return Message for user that tells if everything processed right
     */
    String handleUntrackCommand(String userMessage, User user) {
        var args = userMessage.split(" ");
        if (args.length != 2) {
            return "This command should have 1 parameter";
        }
        var currencyCode = args[1];
        try {
            var trackedCurrency = currencyTrackService.findTrackedCurrency(user.getId(), currencyCode);
            if (trackedCurrency == null) {
                return "No such currency in the tracked list";
            }
            currencyTrackService.deleteTrackedCurrency(trackedCurrency);
            return localizer.localize("This tracking request has been successfully cancelled", user.getLanguageCode()) + "\n" + trackedCurrency.toString();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return "Internal bot error, try to use this command later";
        }
    }

    /**
     * Tries to download json from the net and parse it
     *
     * @return True if downloaded was successful and false if not
     */
    private boolean tryUpdateJsonModel() {
        var mapper = new ObjectMapper();
        try {
            var webPage = webService.getPageAsString(JSON_PAGE_ADDRESS, "UTF-8");
            currModel = mapper.readValue(webPage, CurrenciesJsonModel.class);
            return true;
        } catch (JsonProcessingException jException) {
            LOGGER.error("Error while building CurrenciesJsonModel instance", jException);
        } catch (Exception e) {
            LOGGER.error("Error while downloading page", e);
        }
        return false;
    }

    /**
     * Runs a bot
     *
     * @throws Exception Messenger starting exception
     */
    public void run() throws Exception {
        messenger.run();
    }
}
