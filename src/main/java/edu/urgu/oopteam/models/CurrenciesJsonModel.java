package edu.urgu.oopteam.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import javassist.NotFoundException;

import java.math.BigDecimal;
import java.util.Map;

public class CurrenciesJsonModel {
    @JsonProperty("Date")
    private String date;
    @JsonProperty("PreviousDate")
    private String previousDate;
    @JsonProperty("PreviousURL")
    private String previousURL;
    @JsonProperty("Timestamp")
    private String timestamp;
    @JsonProperty("Valute")
    private Map<String, CurrencyData> currencyDataMap;

    /**
     * Gets exchange rate of the specified currency
     *
     * @param currencyCode Code of the needed currency
     * @return Amount of rubles
     * @throws NotFoundException Exception connected with non-existing currency code
     */
    public BigDecimal getExchangeRate(String currencyCode) throws NotFoundException {
        if ("rub".equals(currencyCode.toLowerCase()))
            return new BigDecimal(1);
        if (currencyDataMap.containsKey(currencyCode.toUpperCase()))
            return new BigDecimal(currencyDataMap.get(currencyCode.toUpperCase()).getValue());
        for (CurrencyData currency : currencyDataMap.values()) {
            if (currency.getName().equalsIgnoreCase(currencyCode))
                return new BigDecimal(currency.getValue());
        }
        throw new NotFoundException("Не найдено валюты с указанным кодом.");
    }
}