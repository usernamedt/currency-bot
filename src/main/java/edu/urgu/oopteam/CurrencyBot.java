package edu.urgu.oopteam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.models.CurrenciesJsonModel;
import edu.urgu.oopteam.services.*;
import edu.urgu.oopteam.viewmodels.BotReponses.*;
import javassist.NotFoundException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@EnableScheduling
public class CurrencyBot {
    private final static String HELP_MESSAGE = "Hello, it's Currency Bot!" +
            "\r\nI can show you some exchange rates. Use commands below:" +
            "\r\n/help - to show this message and possible commands" +
            "\r\n/curr {currency code} {currency code} - to show specified pair exchange rate" +
            "\r\n/track {currency code} {currency code} {delta} - start to track specified currency pair and notify if it changes more/less than delta" +
            "\r\n/untrack {currency code} {currency code} - stop to track specified currency pair" +
            "\r\n/allTracked - show all being tracked currencies" +
            "\r\n/lang {language code} - set language (available languages: ru, en)" +
            "\r\n/exchange {currency code} {city: moskva / ekaterinburg / sankt-peterburg}";
    private final static String UNKNOWN_REQ_MESSAGE = "I don't understand you, check if required command matches one of enlisted in /help message";
    private final static String JSON_PAGE_ADDRESS = "https://www.cbr-xml-daily.ru/daily_json.js";

    // Logger
    private static final Logger LOGGER = Logger.getLogger(CurrencyBot.class);

    CurrenciesJsonModel currModel;

    // Spring services
    private ICurrencyTrackService currencyTrackService;
    private IUserService userService;
    private ITranslationService localizer;
    private ICashExchangeService currencyCashExchangeService;
    private WebService webService;

    @Autowired
    public CurrencyBot(ICurrencyTrackService currencyTrackService,
                       IUserService userService,
                       ITranslationService localizer,
                       ICashExchangeService currencyCashExchangeService,
                       WebService webService
    ) {
        this.currencyTrackService = currencyTrackService;
        this.userService = userService;
        this.localizer = localizer;
        this.currencyCashExchangeService = currencyCashExchangeService;
        this.webService = webService;
    }

    /**
     * Notifies users which are tracking some currencies if delta is greater than requested
     */
    public List<Message> getNotifyMessages() {
        // Update current json model to fetch actual data
        if (!tryUpdateJsonModel()) {
            return new ArrayList<>();
        }

        var result = new ArrayList<Message>();
        var requests = currencyTrackService.findAll();
        requests.forEach(request -> {
            try {
                //var currentRate = currModel.getExchangeRate(request.getFirstCurrencyCode());
                var firstRate = currModel.getExchangeRate(request.getFirstCurrencyCode());
                var secondRate = currModel.getExchangeRate(request.getSecondCurrencyCode());

                var currentRate = firstRate.divide(secondRate, 5, RoundingMode.DOWN);
                var currentDelta = currentRate.subtract(request.getBaseRate());
                if ((request.getDelta().multiply(currentDelta.subtract(request.getDelta()))).compareTo(BigDecimal.ZERO) >= 0) {
                    var localizedMessage =
                            localizer.localize("Rate of your tracked currency pair ", request.getUser().getLanguage())
                                    + request.getFirstCurrencyCode().toUpperCase() + "/"
                                    + request.getSecondCurrencyCode().toUpperCase() + " " +
                                    localizer.localize("has changed from ", request.getUser().getLanguage()) +
                                    request.getBaseRate().toPlainString() + " " + request.getSecondCurrencyCode() +
                                    localizer.localize(" to ", request.getUser().getLanguage())
                                    + currentRate.toPlainString() +
                                    ", delta is " + currentDelta.toPlainString();
                    result.add(new Message(request.getUser().getChatId(), localizedMessage));
                    currencyTrackService.deleteTrackedPair(request);
                }
            } catch (NotFoundException e) {
                LOGGER.error(e.getMessage());
            }
        });
        return result;
    }


    /**
     * Handles /exchange command
     *
     * @param message User's message
     * @return Reply to a user
     */
    public IBotResponse handleExchangeCommand(Message message) {
        var user = userService.getExistingOrNewUser(message.getChatId());
        var messageArgs = message.getMessageBody().split(" ");
        if (messageArgs.length != 3) {
            return new StringResponse(UNKNOWN_REQ_MESSAGE);
        }
        try {
            var bestExchangeRate = currencyCashExchangeService.getCashExchangeRate(messageArgs[1], messageArgs[2]);
            var responseMessage = localizer.localize("Best cash exchange rates:\n", user.getLanguage()) +
                    localizer.localize("Buy rates:\n", user.getLanguage()) +
                    bestExchangeRate.getBuyBankName() + " - " + bestExchangeRate.getBuyRate() + "\n" +
                    localizer.localize("Sell rates:\n", user.getLanguage()) +
                    bestExchangeRate.getSellBankName() + " - " + bestExchangeRate.getSellRate();

            return new ExchangeResponse(bestExchangeRate, responseMessage);
        } catch (Exception e) {
            return new StringResponse("Bad luck: \n" + e.toString());
        }
    }

    /**
     * Handles /lang command (which changes language to specified)
     *
     * @param message User's message
     * @return Reply to a user
     */
    public String handleLangCommand(Message message) {
        var user = userService.getExistingOrNewUser(message.getChatId());
        var messageArgs = message.getMessageBody().split(" ");
        if (messageArgs.length != 2) {
            return localizer.localize(UNKNOWN_REQ_MESSAGE, user.getLanguage());
        }
        if (localizer.languageExists(Language.getLanguageFromLangCode(messageArgs[1]))) {
            userService.setLanguage(message.getChatId(), Language.getLanguageFromLangCode(messageArgs[1]));
            return localizer.localize("Your language has been successfully changed", user.getLanguage());
        }
        return localizer.localize("No such language supported", user.getLanguage());
    }

    /**
     * Handles /curr command (for more info use documentation)
     *
     * @param message User's message
     * @return Reply to a user (exchange rate for a currency)
     */
    public IBotResponse handleCurrCommand(Message message) {
        var user = userService.getExistingOrNewUser(message.getChatId());
        var messageArgs = message.getMessageBody().split(" ");

        if (messageArgs.length == 2 || messageArgs.length == 3) {
            var firstCurrCode = messageArgs[1];
            var secondCurrCode = messageArgs.length == 3 ? messageArgs[2] : "rub";

            try {
                var firstRate = currModel.getExchangeRate(firstCurrCode);
                var secondRate = currModel.getExchangeRate(secondCurrCode);

                return new CurrResponse(firstRate.divide(secondRate, 5, RoundingMode.DOWN),
                        secondCurrCode.toUpperCase());
            } catch (NotFoundException e) {
                return new StringResponse(
                        localizer.localize("I don't know this currency, please check supporting currencies",
                                user.getLanguage()));
            }
        }
        return new StringResponse(localizer.localize(UNKNOWN_REQ_MESSAGE, user.getLanguage()));
    }


    /**
     * Handles /track command (for more info use documentation)
     *
     * @param message User's message
     * @return Message for user that tells if everything processed right
     */
    public IBotResponse handleTrackCommand(Message message) {
        var user = userService.getExistingOrNewUser(message.getChatId());
        var messageArgs = message.getMessageBody().split(" ");
        if (messageArgs.length != 4) {
            return new StringResponse(localizer.localize("This command should have 3 parameters",
                    user.getLanguage()));
        }
        var firstCurrCode = messageArgs[1].toLowerCase();
        var secondCurrCode = messageArgs[2].toLowerCase();
        var delta = new BigDecimal(messageArgs[3]);
        BigDecimal currExchangeRate;
        try {
            var firstRate = currModel.getExchangeRate(firstCurrCode);
            var secondRate = currModel.getExchangeRate(secondCurrCode);

            currExchangeRate = firstRate.divide(secondRate, 5, RoundingMode.DOWN);
        } catch (NotFoundException e) {
            return new StringResponse(e.getMessage());
        }

        try {
            var trackedCurrency = currencyTrackService.findTrackedPair(user.getId(), firstCurrCode, secondCurrCode);
            if (trackedCurrency == null) {
                var trackRequest = currencyTrackService
                        .addTrackedPair(currExchangeRate, firstCurrCode, secondCurrCode, delta, user);
                return new TrackResponse(trackRequest,
                        localizer.localize("New request added", user.getLanguage()));
            }
            currencyTrackService.updateTrackedPair(trackedCurrency, delta, currExchangeRate);
            return new TrackResponse(trackedCurrency, localizer.localize("Existing request has been updated", user.getLanguage()));

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return new StringResponse(localizer.localize("Internal bot error, try to use this command later", user.getLanguage()));
        }
    }

    /**
     * Handles /untrack command (for more info use documentation)
     *
     * @param message User's message
     * @return Message for user that tells if everything processed right
     */
    public IBotResponse handleUntrackCommand(Message message) {
        var user = userService.getExistingOrNewUser(message.getChatId());
        var messageArgs = message.getMessageBody().split(" ");
        if (messageArgs.length != 3) {
            return new StringResponse(
                    localizer.localize("This command should have 2 parameters", user.getLanguage()));
        }
        var firstCurrencyCode = messageArgs[1];
        var secondCurrencyCode = messageArgs[2];
        try {
            var trackedCurrency = currencyTrackService
                    .findTrackedPair(user.getId(), firstCurrencyCode, secondCurrencyCode);
            if (trackedCurrency == null) {
                return new StringResponse(
                        localizer.localize("No such currency in the tracked list", user.getLanguage()));
            }
            currencyTrackService.deleteTrackedPair(trackedCurrency);
            return new TrackResponse(trackedCurrency,
                    localizer.localize("This tracking request has been successfully cancelled",
                            user.getLanguage()));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return new StringResponse(localizer.localize("Internal bot error, try to use this command later",
                    user.getLanguage()));
        }
    }


    /**
     * Handles /alltracked command (for more info use documentation)
     *
     * @param message User's message
     * @return Message for user that tells if everything processed right
     */
    public IBotResponse handleAllTrackedCommand(Message message) {
        var user = userService.getExistingOrNewUser(message.getChatId());
        var userRequests = currencyTrackService.findAllByUserId(user.getId());
        if (userRequests.isEmpty()) {
            return new StringResponse(localizer.localize("You don't have any tracking requests yet", user.getLanguage()));
        }
        return new AllTrackedResponse(userRequests, localizer.localize("Your current tracking requests:", user.getLanguage()));
    }


    /**
     * Handles /alltracked command (for more info use documentation)
     *
     * @return Message for user that tells if everything processed right
     */
    public String handleHelpCommand(Message message) {
        var user = userService.getExistingOrNewUser(message.getChatId());
        return localizer.localize(HELP_MESSAGE, user.getLanguage());
    }

    /**
     * Handles /alltracked command (for more info use documentation)
     *
     * @return Message for user that tells if everything processed right
     */
    public String getUnknownReqMessage(Message message) {
        var user = userService.getExistingOrNewUser(message.getChatId());
        return localizer.localize(UNKNOWN_REQ_MESSAGE, user.getLanguage());
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
}
