package edu.urgu.oopteam.crud.model;

import edu.urgu.oopteam.Language;

import javax.persistence.*;
import java.util.Objects;

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
     * @param chatId   User's chat ID
     * @param language Code of the language used by the user
     */
    public User(long chatId, Language language) {
        this.chatId = chatId;
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id &&
                chatId == user.chatId &&
                language == user.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, language);
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

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", language=" + language +
                '}';
    }
}
