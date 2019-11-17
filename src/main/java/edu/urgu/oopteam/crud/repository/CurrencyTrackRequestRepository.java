package edu.urgu.oopteam.crud.repository;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyTrackRequestRepository extends JpaRepository<CurrencyTrackRequest, Long>{
     /**
     * @param chatId User's chat ID
     * @param currencyCode Code of the required currency
     * @return List of matching requests
     */
    @Query("SELECT r FROM CurrencyTrackRequest r WHERE r.chatId = :chatId AND r.currencyCode = :currencyCode")
    List<CurrencyTrackRequest> findTrackedCurrencies(@Param("chatId") long chatId, @Param("currencyCode") String currencyCode);
    List<CurrencyTrackRequest> getAllByChatId(final Long chatId);
}