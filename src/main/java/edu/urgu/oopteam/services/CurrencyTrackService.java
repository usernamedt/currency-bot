package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.repository.CurrencyTrackRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

@Service
public class CurrencyTrackService implements ICurrencyTrackService {
    @Autowired
    private CurrencyTrackRequestRepository currencyTrackRequestRepository;

    @Override
    public List<CurrencyTrackRequest> findAll() {
        var trackRequests = (List<CurrencyTrackRequest>) currencyTrackRequestRepository.findAll();
        return trackRequests;
    }

    @Override
    public List<CurrencyTrackRequest> findAllByChatId(long chatId){
        var trackRequest = findAll();
        var result = new ArrayList<CurrencyTrackRequest>();
        for (var request : trackRequest) {
            if (request.getChatId() == chatId) {
                result.add(request);
            }
        }
        return result;
    }

    @Override
    public CurrencyTrackRequest addTrackedCurrency(long chatId, double baseRate, String currencyCode, double delta) {
        var trackRequest = new CurrencyTrackRequest(chatId, baseRate, currencyCode, delta);
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
    public CurrencyTrackRequest findTrackedCurrency(long chatId, String currencyCode) {
        var allRequests = findAll();
        for (var request : allRequests) {
            if (request.getChatId() == chatId && request.getCurrencyCode().equals(currencyCode)) {
                return request;
            }
        }
        return null;
    }


}
