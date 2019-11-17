package edu.urgu.oopteam.crud.model;

import javax.persistence.*;
import java.text.MessageFormat;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "chat_id", nullable = false)
    private long chatId;
    @Column(name = "language_code", nullable = false)
    private String languageCode;

    /**
     * Needed for Spring to map entities from database !!!!!!!!!!!!!!!!!!!!!!DONOTDELETE
     */
    public User(){

    }

    /**
     * @param chatId User's chat ID
     * @param languageCode Code of the language used by the user
     */
    public User(long chatId, String languageCode) {
        this.chatId = chatId;
        this.languageCode = languageCode;
    }

    public String getLanguageCode(){
        return languageCode;
    }
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public long getChatId() {
        return chatId;
    }
    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return MessageFormat.format("[id= {0}, chatId= {1}, languageCode= {2}]", id, chatId, languageCode);
    }

}
