package edu.urgu.oopteam.viewmodels.BotReponses;

public class CurrResponse implements IBotResponse {
    public final double exchangeRate;
    // currently bot supports only RUB
    public final String currencyCode = "RUB";

    public CurrResponse(double exchangeRate){
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String getMessage() {
        return exchangeRate + " " + currencyCode;
    }
}
