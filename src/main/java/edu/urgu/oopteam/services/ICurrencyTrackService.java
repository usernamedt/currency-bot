package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface ICurrencyTrackService {
    List<CurrencyTrackRequest> findAll();

    /**
     * Adds new tracking request to database
     *
     * @param baseRate   Exchange rate on the moment of request
     * @param firstCode  Code of the first currency that needs to be tracked
     * @param secondCode Code of the second currency that needs to be tracked
     * @param delta      Minimal difference between exchange rates that we need to notify about
     * @param user       User object
     * @return Request that was just added
     */
    CurrencyTrackRequest addTrackedPair(BigDecimal baseRate, String firstCode,
                                        String secondCode, BigDecimal delta, User user);

    /**
     * Updates existing tracking request
     *
     * @param request          Request to update
     * @param delta            New minimal difference between exchange rates that we need to notify about
     * @param currExchangeRate New exchange rate on the moment of request
     */
    void updateTrackedPair(CurrencyTrackRequest request, BigDecimal delta, BigDecimal currExchangeRate);

    List<CurrencyTrackRequest> findAllByUserId(long chatId);

    /**
     * Deletes tracking request from database
     *
     * @param request Request to delete
     */
    void deleteTrackedPair(CurrencyTrackRequest request);

    /**
     * Finds request in database with specified currency and user's chat ID
     *
     * @param chatId     User's chat ID
     * @param firstCode  Code of the first currency
     * @param secondCode Code of the second currency
     * @return Found request from database
     * @throws SQLException Exception if we have several records with such parameters in database
     */
    CurrencyTrackRequest findTrackedPair(long chatId, String firstCode, String secondCode) throws SQLException;
}
