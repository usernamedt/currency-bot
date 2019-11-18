package edu.urgu.oopteam;

public interface IMessenger {
    /**
     * Sends message to a user
     *
     * @param chatId  User's chat ID
     * @param message Sending message
     */
    void sendMessage(Long chatId, String message);

    /**
     * Sets handler for a user update
     *
     * @param handler Handler (Implementation of TelegramUpdateHandler)
     */
    void setUpdateHandler(TelegramUpdateHandler handler);

    /**
     * Runs messenger
     *
     * @throws Exception Messenger starting exception
     */
    void run() throws Exception;
}
