package edu.urgu.oopteam;

public enum Language {
    ENGLISH {
        public String toString(){
            return "en";
        }
    },
    RUSSIAN {
        public String toString() {
            return "ru";
        }
    };

    public static Language getLanguageFromLangCode(String langCode) {
        if ("ru".equals(langCode)) {
            return Language.RUSSIAN;
        }
        return Language.ENGLISH;
    }
}
