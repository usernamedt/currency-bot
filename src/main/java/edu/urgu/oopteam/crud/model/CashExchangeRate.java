package edu.urgu.oopteam.crud.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "cashExchangeRates")
public class CashExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "city_name", nullable = false)
    private String city;

    @Column(name = "buy_rate", nullable = false)
    private double buyRate;

    @Column(name = "sell_rate", nullable = false)
    private double sellRate;

    @Column(name = "fetch_time", nullable = false)
    private Date fetchTime;

    public CashExchangeRate() {

    }

    public CashExchangeRate(String currencyCode, String city, double buyRate, double sellRate, Date fetchTime) {
        this.currencyCode = currencyCode;
        this.city = city;
        this.buyRate = buyRate;
        this.sellRate = sellRate;
        this.fetchTime = fetchTime;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(double buyRate) {
        this.buyRate = buyRate;
    }

    public double getSellRate() {
        return sellRate;
    }

    public void setSellRate(double sellRate) {
        this.sellRate = sellRate;
    }

    public Date getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Date fetchTime) {
        this.fetchTime = fetchTime;
    }
}
