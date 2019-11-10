package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;

import java.sql.SQLException;
import java.util.List;

public interface ICurrencyTrackService {
    List<CurrencyTrackRequest> findAll();
    CurrencyTrackRequest addTrackedCurrency(long chatId, double baseRate, String currencyCode, double delta);
    void updateTrackedCurrency(CurrencyTrackRequest entity, double delta, double currExchangeRate);
    CurrencyTrackRequest findTrackedCurrency(long chatId, String currencyCode) throws SQLException;
    List<CurrencyTrackRequest> findAllByChatId(long chatId);
    CurrencyTrackRequest deleteTrackedCurrency(CurrencyTrackRequest request);
    List<CurrencyTrackRequest> findTrackedCurrencyFast(long chatId, String currencyCode);
}
