package edu.urgu.oopteam.crud.repository;

import edu.urgu.oopteam.crud.model.Phrase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Translations  extends JpaRepository<Phrase, Long> {
}
