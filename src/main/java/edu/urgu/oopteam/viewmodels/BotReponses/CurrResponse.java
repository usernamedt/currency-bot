package edu.urgu.oopteam.viewmodels.BotReponses;

import java.math.BigDecimal;

public class CurrResponse implements IBotResponse {
    public final BigDecimal exchangeRate;
    // currently bot supports only RUB
    public final String currencyCode = "RUB";

    public CurrResponse(BigDecimal exchangeRate){
        this.exchangeRate = exchangeRate;
    }

    @Override
    public String getMessage() {
        return exchangeRate + " " + currencyCode;
    }
}
