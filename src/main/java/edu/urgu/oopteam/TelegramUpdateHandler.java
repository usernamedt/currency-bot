package edu.urgu.oopteam;

public interface TelegramUpdateHandler {
    void handleUpdate(Long chatId, String message);
}
