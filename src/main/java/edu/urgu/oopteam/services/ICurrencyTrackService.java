package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;

import java.sql.SQLException;
import java.util.List;

public interface ICurrencyTrackService {
    List<CurrencyTrackRequest> findAll();
    CurrencyTrackRequest addTrackedCurrency(long chatId, double baseRate, String currencyCode, double delta, User user);
    void updateTrackedCurrency(CurrencyTrackRequest entity, double delta, double currExchangeRate);
    List<CurrencyTrackRequest> findAllByChatId(long chatId);
    CurrencyTrackRequest deleteTrackedCurrency(CurrencyTrackRequest request);
    List<CurrencyTrackRequest> findTrackedCurrency(long chatId, String currencyCode);
}
