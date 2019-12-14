package edu.urgu.oopteam.viewmodels;

import java.text.MessageFormat;

/**
 * Holds currency buy/sell information (rate and bank name)
 */
public class BuySellExchangeRates {
    private final ExchangeData buyData;
    private final ExchangeData sellData;


    public BuySellExchangeRates(ExchangeData buyData, ExchangeData sellData) {
        this.buyData = buyData;
        this.sellData = sellData;
    }

    public ExchangeData getBuyData() {
        return buyData;
    }

    public ExchangeData getSellData() {
        return sellData;
    }

    @Override
    public String toString() {
        return MessageFormat.format("BuySellExchangeRates[buyData={0}, sellData={1}]", buyData, sellData);
    }
}