package edu.urgu.oopteam.viewmodels;

import java.math.BigDecimal;

/**
 * Holds currency exchange rate and associated bank name
 */
public class ExchangeData {
    private String bankName;
    private BigDecimal rate;

    public ExchangeData(String bankName, BigDecimal rate) {
        this.bankName = bankName;
        this.rate = rate;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}