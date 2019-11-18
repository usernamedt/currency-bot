package edu.urgu.oopteam;

import edu.urgu.oopteam.services.ConfigurationSettings;
import edu.urgu.oopteam.services.FileService;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Please provide path to config.properties file");
            return;
        }

        // load settings from .properties file
        var settings = new ConfigurationSettings(args[0]);

        //configure log4j
        Properties props = settings.getLoggerProperties();
        try {
            var propertiesFile = FileService.getInstance().readResourceFile("log4j.properties");
            props.load(propertiesFile);
        } catch (IOException e) {
            System.out.println("Error: Cannot load configuration file ");
        }
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);

        // run spring app
        var springApplication = new SpringApplication(Main.class);
        springApplication.setDefaultProperties(settings.getSpringProperties());
        ApplicationContext applicationContext = springApplication.run();

        // telegram api context init
        ApiContextInitializer.init();
        var telegramBot = new TelegramCurrencyBot(settings);
        // init currency bot (he'll be alive)
        var currencyBot = new CurrencyBot(applicationContext, telegramBot, settings);
        currencyBot.run();
    }
}
