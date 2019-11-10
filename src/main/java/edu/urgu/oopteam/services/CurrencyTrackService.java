package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;
import edu.urgu.oopteam.crud.repository.CurrencyTrackRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

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
    public List<CurrencyTrackRequest> findAllByChatId(long chatId){
        return currencyTrackRequestRepository.getAllByChatId(chatId);
    }

    public List<CurrencyTrackRequest> findTrackedCurrency(long chatId, String currencyCode) {
        return currencyTrackRequestRepository.findTrackedCurrencyFast(chatId, currencyCode);
    }

    @Override
    public CurrencyTrackRequest addTrackedCurrency(long chatId, double baseRate, String currencyCode, double delta, User user) {
        var trackRequest = new CurrencyTrackRequest(chatId, baseRate, currencyCode, delta, user);
        currencyTrackRequestRepository.save(trackRequest);
        return trackRequest;
    }

    @Override
    public void updateTrackedCurrency(CurrencyTrackRequest entity, double delta, double currExchangeRate) {
        entity.setDelta(delta);
        entity.setBaseRate(currExchangeRate);
        currencyTrackRequestRepository.save(entity);
    }

    @Override
    public CurrencyTrackRequest deleteTrackedCurrency(CurrencyTrackRequest request) {
        currencyTrackRequestRepository.delete(request);
        return request;
    }
}
