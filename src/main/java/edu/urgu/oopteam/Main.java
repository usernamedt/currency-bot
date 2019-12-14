package edu.urgu.oopteam;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class Main {
    public static void main(String[] args) throws Exception {
        if (args.length != 2 || args[0] == null || args[1] == null) {
            System.out.println("Please provide path to application.properties file and log4j.properties file\n" +
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