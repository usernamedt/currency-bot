package edu.urgu.oopteam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Localization {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("LanguageCode")
    private String languageCode;
    @JsonProperty("Translations")
    private Map<String, String> translations;

    public String getName() {
        return name;
    }

    public String getTranslation (String key){
        return translations.getOrDefault(key, key);
    }

    public String getLanguageCode() {
        return languageCode;
    }
}
