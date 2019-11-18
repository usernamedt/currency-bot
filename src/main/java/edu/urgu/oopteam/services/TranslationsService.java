package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.Translation;
import edu.urgu.oopteam.crud.repository.TranslationsRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TranslationsService implements ITranslationService {

    private static final Logger LOGGER = Logger.getLogger(TranslationsService.class);

    private final String mainLanguageCode = "en";

    private final List<String> supportedLangCodes = Arrays.asList(mainLanguageCode, "ru");

    private final List<Translation> translations;

    public TranslationsService(TranslationsRepository translationsRepository) {
        translations = translationsRepository.findAll();
    }

    @Override
    public String localize(String key, String languageCode) {
        if (translations == null) {
            return key;
        }
        var languageLocalization = translations.stream()
                .filter(x -> x.getByLangCode(mainLanguageCode).equals(key))
                .collect(Collectors.toList());
        return languageLocalization.size() == 0 ? key : languageLocalization.get(0).getByLangCode(languageCode);
    }

    @Override
    public boolean languageExists(String languageCode) {
        return supportedLangCodes.contains(languageCode);
    }
}
