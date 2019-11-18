package edu.urgu.oopteam.crud.repository;

import edu.urgu.oopteam.crud.model.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationsRepository extends JpaRepository<Translation, Long> {
}
