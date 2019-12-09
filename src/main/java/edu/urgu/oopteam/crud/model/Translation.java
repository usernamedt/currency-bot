package edu.urgu.oopteam.crud.model;

import edu.urgu.oopteam.Language;

import javax.persistence.*;
import java.text.MessageFormat;

@Entity
@Table(name = "translations")
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "english", nullable = false, columnDefinition = "nvarchar(2000)")
    private String en;
    @Column(name = "russian", nullable = false, columnDefinition = "nvarchar(2000)")
    private String ru;

    public Translation() {

    }

    public String getByLanguage(Language language) {
        if (language == Language.RUSSIAN)
            return ru;
        return en;
    }

    @Override
    public String toString() {
        return MessageFormat.format("[id= {0}, en= {1}, ru= {2}]", id, en, ru);
    }
}
