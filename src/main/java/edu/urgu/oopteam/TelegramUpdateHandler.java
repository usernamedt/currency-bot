package edu.urgu.oopteam;

public interface TelegramUpdateHandler {
    /**
     * Handles the user's message
     *
     * @param chatId  User's chat ID
     * @param message User's message
     */
    void handleUpdate(Long chatId, String message);
}
