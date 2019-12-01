package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;

import java.sql.SQLException;
import java.util.List;

public interface ICurrencyTrackService {
    List<CurrencyTrackRequest> findAll();

    /**
     * Adds new tracking request to database
     *
     * @param baseRate     Exchange rate on the moment of request
     * @param currencyCode Code of the currency that needs to be tracked
     * @param delta        Minimal difference between exchange rates that we need to notify about
     * @param user         User object
     * @return Request that was just added
     */
    CurrencyTrackRequest addTrackedCurrency(double baseRate, String currencyCode, double delta, User user);

    /**
     * Updates existing tracking request
     *
     * @param request          Request to update
     * @param delta            New minimal difference between exchange rates that we need to notify about
     * @param currExchangeRate New exchange rate on the moment of request
     */
    void updateTrackedCurrency(CurrencyTrackRequest request, double delta, double currExchangeRate);

    List<CurrencyTrackRequest> findAllByUserId(long chatId);

    /**
     * Deletes tracking request from database
     *
     * @param request Request to delete
     */
    void deleteTrackedCurrency(CurrencyTrackRequest request);

    /**
     * Finds request in database with specified currency and user's chat ID
     *
     * @param chatId       User's chat ID
     * @param currencyCode Code of the currency
     * @return Found request from database
     * @throws SQLException Exception if we have several records with such parameters in database
     */
    CurrencyTrackRequest findTrackedCurrency(long chatId, String currencyCode) throws SQLException;
}
