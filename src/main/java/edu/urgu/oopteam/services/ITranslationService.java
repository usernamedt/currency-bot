package edu.urgu.oopteam.services;

public interface ITranslationService {
    /**
     * Returns translated value by provided key using languageCode
     *
     * @param key          unique key of phrase (now using english phrase variant as a key)
     * @param languageCode code of the language ("en", "ru", etc)
     * @return translated phrase
     */
    String localize(String key, String languageCode);

    /**
     * Check if specified language exists
     *
     * @param languageCode code of the language ("en", "ru", etc)
     * @return true if exists, otherwise false
     */
    boolean languageExists(String languageCode);
}
