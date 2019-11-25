package edu.urgu.oopteam.crud.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cashexchangerates")
public class CashExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "currency_code", nullable = false)
    private String currencyCode;

    @Column(name = "city_name", nullable = false)
    private String city;

    @Column(name = "buy_bank_name", nullable = false, columnDefinition = "nvarchar(2000)")
    private String buyBankName;

    @Column(name = "sell_bank_name", nullable = false, columnDefinition = "nvarchar(2000)")
    private String sellBankName;

    @Column(name = "buy_rate", nullable = false)
    private double buyRate;

    @Column(name = "sell_rate", nullable = false)
    private double sellRate;

    @Column(name = "fetch_time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fetchTime;


    public CashExchangeRate() {

    }

    public CashExchangeRate(String currencyCode, String city, double buyRate, String buyBankName,
                            double sellRate, String sellBankName, Date fetchTime) {
        this.currencyCode = currencyCode;
        this.city = city;
        this.buyRate = buyRate;
        this.sellRate = sellRate;
        this.buyBankName = buyBankName;
        this.sellBankName = sellBankName;
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

    public String getBuyBankName() {
        return buyBankName;
    }

    public void setBuyBankName(String buyBankName) {
        this.buyBankName = buyBankName;
    }

    public String getSellBankName() {
        return sellBankName;
    }

    public void setSellBankName(String sellBankName) {
        this.sellBankName = sellBankName;
    }
}
