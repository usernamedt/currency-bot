package edu.urgu.oopteam;

import edu.urgu.oopteam.services.ConfigurationSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        // run spring app
        ApplicationContext applicationContext = SpringApplication.run(Main.class);

        // load settings from .properties file
        var settings = new ConfigurationSettings();

        // telegram api context init
        ApiContextInitializer.init();
        var telegramBot = new TelegramCurrencyBot(settings);

        // init currency bot
        CurrencyBot.init(applicationContext, telegramBot);
    }
}
