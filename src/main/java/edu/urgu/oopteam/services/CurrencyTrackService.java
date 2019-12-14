package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;
import edu.urgu.oopteam.crud.repository.CurrencyTrackRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public List<CurrencyTrackRequest> findAllByUserId(long userId) {
        return currencyTrackRequestRepository.getAllByUserId(userId);
    }

    @Override
    public CurrencyTrackRequest findTrackedPair(long userId, String firstCode, String secondCode) throws SQLException {
        var trackedCurrenciesList = currencyTrackRequestRepository
                .findByUserIdAndFirstCurrencyCodeAndSecondCurrencyCode(userId, firstCode, secondCode);
        if (trackedCurrenciesList.isEmpty()) {
            return null;
        }
        if (trackedCurrenciesList.size() > 1) {
            throw new SQLException("Smth wrong");
        }
        return trackedCurrenciesList.get(0);
    }

    @Override
    public CurrencyTrackRequest addTrackedPair(BigDecimal baseRate, String firstCode, String secondCode, BigDecimal delta, User user) {
        var trackRequest = new CurrencyTrackRequest(baseRate, firstCode, secondCode, delta, user);
        currencyTrackRequestRepository.save(trackRequest);
        return trackRequest;
    }

    @Override
    public void updateTrackedPair(CurrencyTrackRequest request, BigDecimal delta, BigDecimal currExchangeRate) {
        request.setDelta(delta);
        request.setBaseRate(currExchangeRate);
        currencyTrackRequestRepository.save(request);
    }

    @Override
    public void deleteTrackedPair(CurrencyTrackRequest request) {
        currencyTrackRequestRepository.delete(request);
    }
}
