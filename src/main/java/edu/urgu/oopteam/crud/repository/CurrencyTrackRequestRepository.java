package edu.urgu.oopteam.crud.repository;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyTrackRequestRepository extends JpaRepository<CurrencyTrackRequest, Long>{
    // Можно пилить такие штуки, и спринг сам будет доставать из базы.
//    List<CurrencyTrackRequest> getAllByCurrencyCode(final String currencyCode, PageRequest pageRequest);
    List<CurrencyTrackRequest> getAllByChatId(final Long chatId);
}