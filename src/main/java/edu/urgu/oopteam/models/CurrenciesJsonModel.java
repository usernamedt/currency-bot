package edu.urgu.oopteam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
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

    public boolean HasValute(String currencyCode){
        if (this.GetValuteMap().containsKey(currencyCode.toUpperCase()))
            return true;
        for (CurrencyData currency: this.GetValuteMap().values()) {
            if (currency.getName().toLowerCase().equals(currencyCode.toLowerCase()))
                return true;
        }
        return false;
    }

    public String GetExchangeRate(String currencyCode){
        if (this.GetValuteMap().containsKey(currencyCode.toUpperCase()))
            return this.GetValuteMap().get(currencyCode.toUpperCase()).getValue();
        for (CurrencyData currency: this.GetValuteMap().values()) {
            if (currency.getName().toLowerCase().equals(currencyCode.toLowerCase()))
                return currency.getValue();
        }
        return "";
    }

    public Map<String, CurrencyData> GetValuteMap(){
        return this.valute;
    }


}