package edu.urgu.oopteam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

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
    private Map<String, CurrencyData> valute;

    public static HashMap<String, CurrencyData> getNameToCurrencyDataDict(CurrenciesJsonModel model) {
        var resultDict = new HashMap<String, CurrencyData>();
        for(var currencyCode : model.valute.keySet()) {
            resultDict.put(model.valute.get(currencyCode).getName(), model.valute.get(currencyCode));
        }
        return resultDict;
    }

    public boolean hasValute(String currencyCode){
        if (valute.containsKey(currencyCode.toUpperCase()))
            return true;
        for (CurrencyData currency: valute.values()) {
            if (currency.getName().toLowerCase().equals(currencyCode.toLowerCase()))
                return true;
        }
        return false;
    }

    public String getExchangeRate(String currencyCode){
        if (valute.containsKey(currencyCode.toUpperCase()))
            return valute.get(currencyCode.toUpperCase()).getValue();
        for (CurrencyData currency: valute.values()) {
            if (currency.getName().toLowerCase().equals(currencyCode.toLowerCase()))
                return currency.getValue();
        }
        return "";
    }
}