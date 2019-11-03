package edu.urgu.oopteam;

import edu.urgu.oopteam.services.ConfigurationSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // run spring app
        ApplicationContext applicationContext = SpringApplication.run(Main.class);

        var settings = new ConfigurationSettings();
        // telegram api context init
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        var telegramBot = new TelegramCurrencyBot(settings);
        try {
            botsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        new CurrencyBot(applicationContext, telegramBot);
    }
}
