import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;


public class CurrencyBot extends TelegramLongPollingBot {
    private final static String helpMessage = "Hello, it's CurrencyBot!" +
            "\nI can show you some exchange rates. Use commands below:" +
            "\n/help - to show this message and view possible commands";

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage().getText();
            var chatID = update.getMessage().getChatId();
            if (message.equals("/help") || message.equals("/start")) {
                var reply = new SendMessage().setChatId(chatID).setText(helpMessage);
                try {
                    execute(reply);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return "CurrencySuperBot";
    }

    @Override
    public String getBotToken() {
        try {
            return getTokenFromFile("token.txt");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTokenFromFile(String fileName) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            var tokenFile = new File(resource.getFile());
            var fr = new FileReader(tokenFile);
            var reader = new BufferedReader(fr);
            return reader.readLine();
        }
    }

    private static String getHttpPageContent(String pageAddress, String codePage) throws Exception {
        StringBuilder sb = new StringBuilder();
        URL pageURL = new URL(pageAddress);
        URLConnection uc = pageURL.openConnection();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        uc.getInputStream(), codePage))) {
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                sb.append(inputLine);
            }
        }
        return sb.toString();
    }

    private static void saveFileToResources(String fileName, String content, String fileExtension) throws IOException {
        var saveDirectory = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources";
        var file = new File(saveDirectory, fileName + fileExtension);
        if (!file.createNewFile())
            throw new IOException("Ошибка при создании файла");

        try(var writer = new FileWriter(file.getAbsolutePath(), false))
        {
            writer.write(content);
            writer.flush();
        }
        catch(IOException exception){
            throw new IOException("Ошибка при записи в файл");
//            System.out.println(exception.getMessage());
//            System.exit(-1);
        }
    }
}