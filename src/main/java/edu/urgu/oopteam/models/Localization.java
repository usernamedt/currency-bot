package edu.urgu.oopteam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Localization {
    @JsonProperty("name")
    private String name;
    @JsonProperty("languageCode")
    private String languageCode;
    @JsonProperty("translations")
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
