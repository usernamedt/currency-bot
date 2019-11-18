package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;
import edu.urgu.oopteam.crud.repository.CurrencyTrackRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class CurrencyTrackService implements ICurrencyTrackService {

    private final CurrencyTrackRequestRepository currencyTrackRequestRepository;

    @Autowired
    public CurrencyTrackService(CurrencyTrackRequestRepository currencyTrackRequestRepository) {
        this.currencyTrackRequestRepository = currencyTrackRequestRepository;
    }

    @Override
    public List<CurrencyTrackRequest> findAll() {
        return currencyTrackRequestRepository.findAll();
    }

    @Override
    public List<CurrencyTrackRequest> findAllByChatId(long chatId) {
        return currencyTrackRequestRepository.getAllByChatId(chatId);
    }

    @Override
    public CurrencyTrackRequest findTrackedCurrency(long chatId, String currencyCode) throws SQLException {
        var trackedCurrenciesList = currencyTrackRequestRepository.findTrackedCurrencies(chatId, currencyCode);
        if (trackedCurrenciesList.isEmpty()) {
            return null;
        }
        if (trackedCurrenciesList.size() > 1) {
            throw new SQLException("Smth wrong");
        }
        return trackedCurrenciesList.get(0);
    }

    @Override
    public CurrencyTrackRequest addTrackedCurrency(double baseRate, String currencyCode, double delta, User user) {
        var trackRequest = new CurrencyTrackRequest(user.getChatId(), baseRate, currencyCode, delta, user);
        currencyTrackRequestRepository.save(trackRequest);
        return trackRequest;
    }

    @Override
    public void updateTrackedCurrency(CurrencyTrackRequest request, double delta, double currExchangeRate) {
        request.setDelta(delta);
        request.setBaseRate(currExchangeRate);
        currencyTrackRequestRepository.save(request);
    }

    @Override
    public void deleteTrackedCurrency(CurrencyTrackRequest request) {
        currencyTrackRequestRepository.delete(request);
    }
}
