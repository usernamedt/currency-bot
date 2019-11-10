package edu.urgu.oopteam.crud.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.text.MessageFormat;

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "chat_id", nullable = false)
    private long chatId;
    @Column(name = "language_code", nullable = false)
    private String languageCode;

    public User(){

    }

    public User(long chatId, String languageCode) {
        this.chatId = chatId;
        this.languageCode = languageCode;
    }

    @Override
    public String toString() {
        return MessageFormat.format("[id= {0}, chatId= {1}, languageCode= {2}]", id, chatId, languageCode);
    }

}
