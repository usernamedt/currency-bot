package edu.urgu.oopteam.viewmodels;

/**
 * Holds currency exchange rate and associated bank name
 */
public class ExchangeData {
    private String bankName;
    private double rate;

    public ExchangeData(String bankName, double rate) {
        this.bankName = bankName;
        this.rate = rate;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}