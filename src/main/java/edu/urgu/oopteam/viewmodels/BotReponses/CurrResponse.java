package edu.urgu.oopteam.viewmodels.BotReponses;

import java.math.BigDecimal;

public class CurrResponse implements IBotResponse {
    public final BigDecimal exchangeRate;
    // currently bot supports only RUB
    public final String currencyCode;

    public CurrResponse(BigDecimal exchangeRate){
        this.exchangeRate = exchangeRate;
        this.currencyCode = "RUB";
    }

    public CurrResponse(BigDecimal exchangeRate, String currencyCode){
        this.exchangeRate = exchangeRate;
        this.currencyCode = currencyCode;
    }

    @Override
    public String getMessage() {
        return exchangeRate + " " + currencyCode;
    }
}
