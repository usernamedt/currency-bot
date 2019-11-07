package edu.urgu.oopteam;

import edu.urgu.oopteam.services.ConfigurationSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {
        // run spring app
        ApplicationContext applicationContext = SpringApplication.run(Main.class);

        // load settings from .properties file
        var settings = new ConfigurationSettings();

        // telegram api context init
        ApiContextInitializer.init();
        var telegramBot = new TelegramCurrencyBot(settings);
        // init currency bot (he'll be alive)
        var currencyBot = new CurrencyBot(applicationContext, telegramBot);
        currencyBot.run();
    }
}
