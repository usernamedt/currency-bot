package edu.urgu.oopteam.crud.repository;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyTrackRequestRepository extends JpaRepository<CurrencyTrackRequest, Long>{
    List<CurrencyTrackRequest> getAllByChatId(final Long chatId);
}