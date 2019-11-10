package edu.urgu.oopteam.crud.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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

    public CurrencyTrackRequest() {

    }

    public CurrencyTrackRequest(Long chatId, double baseRate, String currencyCode, double delta) {
        this.chatId = chatId;
        this.baseRate = baseRate;
        this.currencyCode = currencyCode;
        this.delta = delta;
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


    @Override
    public String toString() {
        return MessageFormat.format("CurrencyTrackRequest " +
                "[id= {0}, chatId= {1}, baseRate= {2}, currencyCode= {3}, delta= {4}",
                id, chatId, baseRate, currencyCode, delta);
    }

}