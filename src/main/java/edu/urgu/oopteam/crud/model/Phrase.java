package edu.urgu.oopteam.crud.model;

import javax.persistence.*;

@Entity
@Table(name = "translations")
public class Phrase {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(name = "english", nullable = false)
    private String engPhrase;
    @Column(name = "russian", nullable = false)
    private String ruPhrase;

    public Phrase(){

    }

    public Phrase(String engPhrase, String ruPhrase) {
        this.engPhrase = engPhrase;
        this.ruPhrase = ruPhrase;
    }

    public String getEngPhrase() {
        return engPhrase;
    }

    public void setEngPhrase(String engPhrase) {
        this.engPhrase = engPhrase;
    }

    public String getRuPhrase() {
        return ruPhrase;
    }

    public void setRuPhrase(String ruPhrase) {
        this.ruPhrase = ruPhrase;
    }
}
