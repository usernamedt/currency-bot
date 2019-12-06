package edu.urgu.oopteam.viewmodels.BotReponses;

import edu.urgu.oopteam.crud.model.CashExchangeRate;
import edu.urgu.oopteam.viewmodels.BuySellExchangeRates;
import edu.urgu.oopteam.viewmodels.ExchangeData;

public class ExchangeResponse implements IBotResponse {
    private final BuySellExchangeRates buySellExchangeRates;
    private final String responseBody;


    public ExchangeResponse(BuySellExchangeRates buySellExchangeRates, String messageBody){
        this.buySellExchangeRates = buySellExchangeRates;
        this.responseBody = messageBody;
    }


    public ExchangeResponse(CashExchangeRate exchangeRate, String responseBody){
        var buyData = new ExchangeData(exchangeRate.getBuyBankName(), exchangeRate.getBuyRate());
        var sellData = new ExchangeData(exchangeRate.getSellBankName(), exchangeRate.getSellRate());
        this.buySellExchangeRates = new BuySellExchangeRates(buyData, sellData);
        this.responseBody = responseBody;
    }

    @Override
    public String getMessage() {
        return responseBody;
    }

    public BuySellExchangeRates getBuySellExchangeRates() {
        return buySellExchangeRates;
    }
}
