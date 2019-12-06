package edu.urgu.oopteam.viewmodels;

import edu.urgu.oopteam.crud.model.CashExchangeRate;

/**
 * Holds currency buy/sell information (rate and bank name)
 */
public class BuySellExchangeRates {
    private final ExchangeData buyData;
    private final ExchangeData sellData;


    public BuySellExchangeRates(ExchangeData buyData, ExchangeData sellData){
        this.buyData = buyData;
        this.sellData = sellData;
    }

    public ExchangeData getBuyData() {
        return buyData;
    }

    public ExchangeData getSellData() {
        return sellData;
    }
}