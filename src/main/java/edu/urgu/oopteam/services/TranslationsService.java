package edu.urgu.oopteam.services;

import edu.urgu.oopteam.Language;
import edu.urgu.oopteam.crud.model.Translation;
import edu.urgu.oopteam.crud.repository.TranslationsRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslationsService implements ITranslationService {

    private static final Language mainLanguage = Language.ENGLISH;

    private final List<Language> supportedLangCodes = Arrays.asList(mainLanguage, Language.RUSSIAN);

    private final List<Translation> translations;

    public TranslationsService(TranslationsRepository translationsRepository) {
        translations = translationsRepository.findAll();
    }

    @Override
    public String localize(String key, Language language) {
        if (translations == null) {
            return key;
        }
        var languageLocalization = translations.stream()
                .filter(x -> x.getByLanguage(mainLanguage).equals(key))
                .collect(Collectors.toList());
        return languageLocalization.isEmpty() ? key : languageLocalization.get(0).getByLanguage(language);
    }

    @Override
    public boolean languageExists(Language language) {
        return supportedLangCodes.contains(language);
    }
}
