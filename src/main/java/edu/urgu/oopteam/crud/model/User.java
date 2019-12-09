package edu.urgu.oopteam.crud.model;

import edu.urgu.oopteam.Language;

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
    @Column(name = "language", nullable = false)
    private Language language;

    /**
     * Needed for Spring to map entities from database !!!!!!!!!!!!!!!!!!!!!!DONOTDELETE
     */
    public User() {

    }

    /**
     * @param chatId       User's chat ID
     * @param language Code of the language used by the user
     */
    public User(long chatId, Language language) {
        this.chatId = chatId;
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public long getId() {return id;}

    @Override
    public String toString() {
        return MessageFormat.format("[id= {0}, chatId= {1}, languageCode= {2}]", id, chatId, language);
    }

}
