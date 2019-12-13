package edu.urgu.oopteam.crud.repository;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyTrackRequestRepository extends JpaRepository<CurrencyTrackRequest, Long> {
    /**
     * @param userId       User's chat ID
     * @param currencyCode Code of the required currency
     * @return List of matching requests
     */
    List<CurrencyTrackRequest> findByUserIdAndCurrencyCode(long userId, String currencyCode);

    List<CurrencyTrackRequest> getAllByUserId(final Long chatId);

    @Override
    void deleteAll();
}