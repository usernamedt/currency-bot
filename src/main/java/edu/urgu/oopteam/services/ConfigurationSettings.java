package edu.urgu.oopteam.services;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationSettings {
    private static final Logger LOGGER = Logger.getLogger(ConfigurationSettings.class);
    private String botUserName;
    private String botToken;
    private String botDataDir;

    private Properties springProperties = new Properties();
    private Properties loggerProperties = new Properties();

    public ConfigurationSettings(String propertiesPath) throws Exception {
        var props = new Properties();
        try {
            var fileInputStream = new FileInputStream(propertiesPath);
            props.load(fileInputStream);
        } catch (IOException e) {
            var message = "Указанный конфигурационный файл (" + propertiesPath + ") не обнаружен";
            LOGGER.error(message, e);
            throw new IOException(message, e);
        }

        try {
            botUserName = props.getProperty("bot.name");

            botToken = props.getProperty("bot.token");
            botDataDir = props.getProperty("bot.data.directory");

            springProperties.put("spring.datasource.url", "jdbc:sqlserver://" + props.getProperty("spring.databaseIp") +
                    ";databaseName=" + props.getProperty("spring.databaseName"));
            springProperties.put("spring.datasource.username", props.getProperty("spring.databaseUsername"));
            springProperties.put("spring.datasource.password", props.getProperty("spring.databasePassword"));

            loggerProperties.put("log4j.appender.file.File", props.getProperty("log.outputPath"));
        } catch (NullPointerException e) {
            throw new Exception("Configuration file is incomplete. Please see example for reference.");
        }
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

    public Properties getSpringProperties() {
        return springProperties;
    }

    public Properties getLoggerProperties() {
        return loggerProperties;
    }
}
