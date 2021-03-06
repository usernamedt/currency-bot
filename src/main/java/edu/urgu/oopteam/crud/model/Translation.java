package edu.urgu.oopteam.crud.model;

import edu.urgu.oopteam.Language;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Translation that = (Translation) o;
        return id == that.id &&
                Objects.equals(en, that.en) &&
                Objects.equals(ru, that.ru);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, en, ru);
    }

    public String getByLanguage(Language language) {
        if (language == Language.RUSSIAN)
            return ru;
        return en;
    }

    @Override
    public String toString() {
        return "Translation{" +
                "en='" + en + '\'' +
                ", ru='" + ru + '\'' +
                '}';
    }
}
