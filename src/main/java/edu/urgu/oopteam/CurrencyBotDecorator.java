package edu.urgu.oopteam;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CurrencyBotDecorator {
    private static final Logger LOGGER = Logger.getLogger(CurrencyBotDecorator.class);
    private final IMessenger messenger;
    private final ExecutorService pool = Executors.newFixedThreadPool(200);
    private final CurrencyBot currencyBot;

    @Autowired
    public CurrencyBotDecorator(IMessenger messenger, CurrencyBot currencyBot) {
        this.messenger = messenger;
        messenger.setUpdateHandler(this::processMessageAsync);
        var jsonUpdateTimer = new Timer();
        var jsonUpdateTask = new TimerTask() {
            @Override
            public void run() {
                notifyTrackedUsers();
            }
        };
        jsonUpdateTimer.scheduleAtFixedRate(jsonUpdateTask, new Date(), 1000 * 60 * 60);
        this.currencyBot = currencyBot;
    }

    /**
     * Notifies users which are tracking some currencies if delta is greater than requested
     */
    private void notifyTrackedUsers() {
        currencyBot.getNotifyMessages().forEach(message ->
            messenger.sendMessage(message.getChatId(), message.getMessageBody()));
    }

    private void processMessageAsync(Long chatID, String userMessage) {
        CompletableFuture.runAsync(() -> processMessage(new Message(chatID, userMessage)), pool);
    }

    /**
     * Parses a command then processes it
     *
     * @param message User's message
     */
    private void processMessage(Message message) {
        var userMessage = message.getMessageBody();
        var chatId = message.getChatId();
        if ("/help".equals(userMessage) || "/start".equals(userMessage)) {
            messenger.sendMessage(chatId, currencyBot.handleHelpCommand(message));
        } else if (userMessage.startsWith("/curr ")) {
            messenger.sendMessage(chatId, currencyBot.handleCurrCommand(message).getMessage());
        } else if (userMessage.startsWith("/track ")) {
            messenger.sendMessage(chatId, currencyBot.handleTrackCommand(message).getMessage());
        } else if (userMessage.startsWith("/untrack ")) {
            messenger.sendMessage(chatId, currencyBot.handleUntrackCommand(message).getMessage());
        } else if (userMessage.equals("/allTracked")) {
            messenger.sendMessage(chatId, currencyBot.handleAllTrackedCommand(message).getMessage());
        } else if (userMessage.startsWith("/lang ")) {
            messenger.sendMessage(chatId, currencyBot.handleLangCommand(message));
        } else if (userMessage.startsWith("/exchange ")) {
            messenger.sendMessage(chatId, currencyBot.handleExchangeCommand(message).getMessage());
        } else {
            messenger.sendMessage(chatId, currencyBot.getUnknownReqMessage(message));
        }
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
