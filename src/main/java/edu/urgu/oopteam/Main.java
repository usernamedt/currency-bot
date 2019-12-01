package edu.urgu.oopteam;

import edu.urgu.oopteam.services.ConfigurationSettings;
import edu.urgu.oopteam.services.FileService;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

import java.io.IOException;
import java.util.Properties;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 1 && args[0] != null) {
            System.out.println("Please provide path to config.properties file");
            return;
        }

        // telegram api context init
        ApiContextInitializer.init();
        // run spring app
        ApplicationContext applicationContext;
        try {
            applicationContext = new SpringApplicationBuilder(Main.class)
                    .properties("spring.config.location=file:///" + args[0] +
                            ",classpath:/application.properties")
                    .build()
                    .run();
        } catch (Exception e) {
            System.out.println("Houston, we've got problems!");
            return;
        }

        // load settings from .properties file
        var settings = applicationContext.getBean(ConfigurationSettings.class);

        //configure log4j
        Properties props = settings.getLoggerProperties();
        try {
            var fileService = applicationContext.getBean(FileService.class);
            var propertiesFile = fileService.readResourceFile("log4j.properties");
            props.load(propertiesFile);
        } catch (IOException e) {
            System.out.println("Error: Cannot load configuration file ");
        }
        LogManager.resetConfiguration();
        PropertyConfigurator.configure(props);

        var telegramBot = applicationContext.getBean(TelegramCurrencyBot.class);

        // init currency bot (he'll be alive)
        var currencyBot = applicationContext.getBean(CurrencyBot.class);
        currencyBot.run();
    }
}
