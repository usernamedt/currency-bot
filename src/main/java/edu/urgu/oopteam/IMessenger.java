package edu.urgu.oopteam;

public interface IMessenger {
    void sendMessage(Long chatId, String message);
    void setUpdateHandler(TelegramUpdateHandler handler);
}
