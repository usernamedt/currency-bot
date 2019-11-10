package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
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

    public List<CurrencyTrackRequest> findTrackedCurrencyFast(long chatId, String currencyCode) {
        return currencyTrackRequestRepository.findTrackedCurrencyFast(chatId, currencyCode);
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
    public CurrencyTrackRequest deleteTrackedCurrency(CurrencyTrackRequest request) {
        currencyTrackRequestRepository.delete(request);
        return request;
    }

    /**
     * @param chatId ID пользователя
     * @param currencyCode Код валюты, по которой надо проверить наличие записи
     * @return Соответствующий CurrencyTrackRequest если запись единственна и null, если записи нет вообще
     * @throws SQLException Если в базе несколько записей, у которыз совпадают chatID и currencyCode
     */
    @Override
    public CurrencyTrackRequest findTrackedCurrency(long chatId, String currencyCode) throws SQLException {
        var oneUserTrackRequests = currencyTrackRequestRepository.getAllByChatId(chatId);
        var result = oneUserTrackRequests.stream()
                .filter(request -> currencyCode.equals(request.getCurrencyCode()))
                .collect(Collectors.toList());
        if (result.size() > 1) {
            throw new SQLException("Several records in database with the same currencyCode and chatId");
        }
        return result.size() == 1 ? result.get(0) : null;
    }
}
