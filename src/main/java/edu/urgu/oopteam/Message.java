package edu.urgu.oopteam;

public class Message {
    private long chatId;
    private String messageBody;

    public Message(long chatId, String messageBody) {
        this.chatId = chatId;
        this.messageBody = messageBody;
    }


    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
