package edu.urgu.oopteam.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class ConfigurationSettings {
    @Value("${bot.name}")
    private String botUserName;
    @Value("${bot.token}")
    private String botToken;
    @Value("${bot.data.directory}")
    private String botDataDir;
//    @Value("${spring.datasource.url}")
//    private String springDatasourceUrl;
//    @Value("${spring.datasource.username}")
//    private String springDatasourceUsername;
//    @Value("${spring.datasource.password}")
//    private String springDatasourcePassword;

    public ConfigurationSettings() {
    }

    public String getBotUserName() {
        return botUserName;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getBotDataDir() {
        return botDataDir;
    }
}
