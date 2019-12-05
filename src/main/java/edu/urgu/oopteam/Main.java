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
        if (args.length != 2 || args[0] == null || args[1] == null) {
            System.out.println("Please provide path to config.properties files\n" +
                    "You can find an example in README.md");
            return;
        }

        // telegram api context init
        ApiContextInitializer.init();

        // run spring app
        ApplicationContext applicationContext;
        try {
            applicationContext = new SpringApplicationBuilder(Main.class)
                    .properties("spring.config.location=file:///" + args[1])
                    .build()
                    .run();
        } catch (Exception e) {
            System.out.println("Houston, we've got problems!");
            return;
        }

        // load settings from .properties file
        var currencyBotDecorator = applicationContext.getBean(CurrencyBotDecorator.class);

        // init currency bot (he'll be alive)
        currencyBotDecorator.run();
    }
}