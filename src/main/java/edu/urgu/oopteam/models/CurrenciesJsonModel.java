package edu.urgu.oopteam.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.Nullable;
import javassist.NotFoundException;

import java.util.HashMap;
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

    public static HashMap<String, CurrencyData> getNameToCurrencyDataDict(CurrenciesJsonModel model) {
        var resultDict = new HashMap<String, CurrencyData>();
        for(var currencyCode : model.currencyDataMap.keySet()) {
            resultDict.put(model.currencyDataMap.get(currencyCode).getName(), model.currencyDataMap.get(currencyCode));
        }
        return resultDict;
    }

    public double getExchangeRate(String currencyCode) throws NotFoundException {
        if (currencyDataMap.containsKey(currencyCode.toUpperCase()))
            return Double.parseDouble(currencyDataMap.get(currencyCode.toUpperCase()).getValue());
        for (CurrencyData currency: currencyDataMap.values()) {
            if (currency.getName().equalsIgnoreCase(currencyCode))
                return Double.parseDouble(currency.getValue());
        }
        throw new NotFoundException("Не найдено валюты с указанным кодом.");
    }
}