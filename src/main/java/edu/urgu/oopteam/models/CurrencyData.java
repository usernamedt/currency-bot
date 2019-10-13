package edu.urgu.oopteam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrencyData {
    @JsonProperty("ID")
    private String id;
    @JsonProperty("NumCode")
    private String numCode;
    @JsonProperty("CharCode")
    private  String charCode;
    @JsonProperty("Nominal")
    private String nominal;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Value")
    private String value;
    @JsonProperty("Previous")
    private String previous;

    public String getName(){
        return name;
    }
    public String getValue(){
        return value;
    }
}
