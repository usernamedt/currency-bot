package edu.urgu.oopteam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class Localization {
    @JsonProperty("Name")
    private String name; // Name of the language
    @JsonProperty("LanguageCode")
    private String languageCode;
    @JsonProperty("Translations")
    private Map<String, String> translations;

    public String getName() {
        return name;
    }

    /**
     * Gets translation of the phrase
     *
     * @param key String that needs to be translated
     * @return Translated string or key string if we have no translations for this phrase
     */
    public String getTranslation(String key) {
        return translations.getOrDefault(key, key);
    }

    public String getLanguageCode() {
        return languageCode;
    }
}
