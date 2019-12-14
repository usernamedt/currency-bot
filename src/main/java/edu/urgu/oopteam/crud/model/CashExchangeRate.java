package edu.urgu.oopteam.crud.model;

import edu.urgu.oopteam.viewmodels.BuySellExchangeRates;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

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
    private BigDecimal buyRate;

    @Column(name = "sell_rate", nullable = false)
    private BigDecimal sellRate;

    @Column(name = "fetch_time", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fetchTime;


    public CashExchangeRate() {

    }

    public CashExchangeRate(String currencyCode, String city, BigDecimal buyRate, String buyBankName,
                            BigDecimal sellRate, String sellBankName, Date fetchTime) {
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

    public BigDecimal getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(BigDecimal buyRate) {
        this.buyRate = buyRate;
    }

    public BigDecimal getSellRate() {
        return sellRate;
    }

    public void setSellRate(BigDecimal sellRate) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CashExchangeRate that = (CashExchangeRate) o;
        return id == that.id &&
                currencyCode.equals(that.currencyCode) &&
                city.equals(that.city) &&
                buyBankName.equals(that.buyBankName) &&
                sellBankName.equals(that.sellBankName) &&
                buyRate.equals(that.buyRate) &&
                sellRate.equals(that.sellRate) &&
                fetchTime.equals(that.fetchTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, currencyCode, city, buyBankName, sellBankName, buyRate, sellRate, fetchTime);
    }

    @Override
    public String toString() {
        return "CashExchangeRate{" +
                "currencyCode='" + currencyCode + '\'' +
                ", city='" + city + '\'' +
                ", buyBankName='" + buyBankName + '\'' +
                ", sellBankName='" + sellBankName + '\'' +
                ", buyRate=" + buyRate +
                ", sellRate=" + sellRate +
                '}';
    }
}
