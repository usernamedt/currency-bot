package edu.urgu.oopteam;

import edu.urgu.oopteam.services.ConfigurationSettings;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        var settings = new ConfigurationSettings();

        // Set up Http proxy
        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);

        botOptions.setProxyHost(settings.getProxyHost());
        botOptions.setProxyPort(settings.getProxyPort());
        // Select proxy type: [HTTP|SOCKS4|SOCKS5] (default: NO_PROXY)
        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        try {
            botsApi.registerBot(new CurrencyBot(settings, botOptions));
//            botsApi.registerBot(new CurrencyBot(settings));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
