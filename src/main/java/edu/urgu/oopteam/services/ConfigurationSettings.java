package edu.urgu.oopteam.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationSettings {
    private static final String PATH_TO_PROPERTIES =
            "src" + File.separator + "main" + File.separator + "resources" + File.separator + "config.properties";
    private String botUserName;
    private String botToken;
    private String botDataDir;
    private String proxyHost;
    private int proxyPort;

    public ConfigurationSettings() {
        var props = new Properties();
        try {
            var fileInputStream = new FileInputStream(PATH_TO_PROPERTIES);
            props.load(fileInputStream);
        } catch (IOException e) {
            System.out.println(
                    "Конфигурационный файл, который должен лежать в директории " + PATH_TO_PROPERTIES + " не обнаружен");
            e.printStackTrace();
        }

        botUserName = props.getProperty("bot.name");
        botToken = props.getProperty("bot.token");
        botDataDir = props.getProperty("bot.data.directory");
        proxyHost = props.getProperty("proxy.host");

        try {
            proxyPort = Integer.parseInt(props.getProperty("proxy.port"));
        } catch (NumberFormatException exception) {
            System.out.println("Порт прокси должен быть типа int");
            exception.printStackTrace();
        }
    }

    public String getBotUserName(){
        return botUserName;
    }
    public String getBotToken(){
        return botToken;
    }
    public String getBotDataDir(){
        return botDataDir;
    }
    public String getProxyHost(){
        return proxyHost;
    }
    public int getProxyPort(){
        return proxyPort;
    }
}
