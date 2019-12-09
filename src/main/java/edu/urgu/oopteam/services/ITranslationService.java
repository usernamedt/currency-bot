package edu.urgu.oopteam.services;

import edu.urgu.oopteam.Language;

public interface ITranslationService {
    /**
     * Returns translated value by provided key using languageCode
     *
     * @param key          unique key of phrase (now using english phrase variant as a key)
     * @param language     language (RUSSIAN, ENGLISH)
     * @return translated phrase
     */
    String localize(String key, Language language);

    /**
     * Check if specified language exists
     *
     * @param language  language (RUSSIAN, ENGLISH)
     * @return true if exists, otherwise false
     */
    boolean languageExists(Language language);
}
