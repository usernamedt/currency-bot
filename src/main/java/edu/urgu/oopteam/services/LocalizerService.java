package edu.urgu.oopteam.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.urgu.oopteam.models.Localization;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LocalizerService {

    private List<Localization> localizations;

    private final String mainLanguageCode = "en";

    public LocalizerService(String localesFolder) {
        try {
            List<String> localeFilenames = getLocaleFilenames(localesFolder);
            localizations = new ArrayList<Localization>();
            for (var fileName: localeFilenames){
                var localization = readLocaleFromFile(fileName);
                if (localization != null){
                    localizations.add(localization);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String localize(String key, String languageCode) {
        if (localizations == null){
            return key;
        }
        var languageLocalization = localizations.stream()
                .filter(x -> x.getLanguageCode().equals(languageCode))
                .collect(Collectors.toList());
        if (languageLocalization.size() == 0) {
            return key;
        }
        return languageLocalization.get(0).getTranslation(key);
    }

    public boolean languageExists(String languageCode) {
        if (languageCode.equals(mainLanguageCode)) {
            return true;
        }
        var localizations = this.localizations.stream()
                .filter(x -> x.getLanguageCode().equals(languageCode))
                .collect(Collectors.toList());
        return localizations.size() > 0;
    }


    private Localization readLocaleFromFile(String fileUri) {
        var mapper = new ObjectMapper();
        try {
            var fileContent = readFile(fileUri);
            return mapper.readValue(fileContent, Localization.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> getLocaleFilenames(String localesFolder) throws IOException{
        Stream<Path> walk = Files.walk(Paths.get(localesFolder));
        return walk.map(Path::toString)
                    .filter(f -> f.matches("^(\\S+)\\.json$")).collect(Collectors.toList());
    }

    /**
     * @param fileUri fileUri
     * @return file content as a string
     */
    private String readFile(String fileUri) throws IOException {
        Scanner sc = new Scanner(new File(fileUri));
        var builder = new StringBuilder();
        while (sc.hasNext()) {
            builder.append(sc.nextLine());
        }
        sc.close();
        return builder.toString();
    }
}
