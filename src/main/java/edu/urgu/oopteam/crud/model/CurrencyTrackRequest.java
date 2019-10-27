package edu.urgu.oopteam.crud.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "currencytrackrequests")
public class CurrencyTrackRequest {

    private long id;
    private long chatId;
    // exchange rate for currency on the time of request
    private double baseRate;
    // currency name
    private String currencyCode;
    // delta
    private double delta;

    public CurrencyTrackRequest() {

    }

    public CurrencyTrackRequest(long chatId, double baseRate, String currencyCode, double delta) {
        this.chatId = chatId;
        this.baseRate = baseRate;
        this.currencyCode = currencyCode;
        this.delta = delta;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "chat_id", nullable = false)
    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    @Column(name = "base_rate", nullable = false)
    public double getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(double baseRate) {
        this.baseRate = baseRate;
    }

    @Column(name = "currency_name", nullable = false)
    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Column(name = "delta", nullable = false)
    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }


    @Override
    public String toString() {
        return "CurrencyTrackRequest [id=" + id + ", chatId=" + chatId + ", baseRate=" + baseRate + ", currencyCode="
                + currencyCode + ", delta=" + delta + "]";
    }

}