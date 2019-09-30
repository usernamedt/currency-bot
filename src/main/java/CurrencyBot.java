import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;


public class CurrencyBot extends TelegramLongPollingBot {
    private final static String helpMessage = "Hello, it's CurrencyBot!" +
            "\nI can show you some exchange rates. Use commands below:" +
            "\n/help - to show this message and view possible commands";

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage().getText();
            var chatID = update.getMessage().getChatId();
            if (message.equals("/help") || message.equals("/start")) {
                var reply = new SendMessage().setChatId(chatID).setText(helpMessage);
                try {
                    execute(reply);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "CurrencySuperBot";
    }

    @Override
    public String getBotToken() {
        try {
            return getTokenFromFile("token.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTokenFromFile(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            var tokenFile = new File(resource.getFile());
            var fr = new FileReader(tokenFile);
            var reader = new BufferedReader(fr);
            return reader.readLine();
        }
    }
}