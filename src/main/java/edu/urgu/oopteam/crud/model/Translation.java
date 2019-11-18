package edu.urgu.oopteam.crud.model;

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

    public Translation(String en, String ru) {
        this.en = en;
        this.ru = ru;
    }

    public String getByLangCode(String code) {
        if (code.equals("ru"))
            return ru;
        return en;
    }

    @Override
    public String toString() {
        return MessageFormat.format("CurrencyTrackRequest " +
                        "[id= {0}, en= {1}, ru= {2}",
                id, en, ru);
    }
}
