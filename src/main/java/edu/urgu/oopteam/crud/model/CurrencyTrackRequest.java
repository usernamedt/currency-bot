package edu.urgu.oopteam.crud.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.text.MessageFormat;

@Entity
@Table(name = "currencytrackrequests")
public class CurrencyTrackRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "chat_id", nullable = false)
    private long chatId;
    // exchange rate for currency on the time of
    @Column(name = "base_rate", nullable = false)
    private double baseRate;
    // currency name
    @Column(name = "currency_name", nullable = false)
    private String currencyCode;
    // delta
    @Column(name = "delta", nullable = false)
    private double delta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    /**
     * Needed for Spring to map entities from database !!!!!!!!!!!!!!!!!!!!!!DONOTDELETE
     */
    public CurrencyTrackRequest() {

    }

    public CurrencyTrackRequest(Long chatId, double baseRate, String currencyCode, double delta, User user) {
        this.chatId = chatId;
        this.baseRate = baseRate;
        this.currencyCode = currencyCode;
        this.delta = delta;
        this.user = user;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }


    public double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }


    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }


    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    @Override
    public String toString() {
        return MessageFormat.format("CurrencyTrackRequest " +
                        "[id= {0}, chatId= {1}, baseRate= {2}, currencyCode= {3}, delta= {4}",
                id, chatId, baseRate, currencyCode, delta);
    }

}