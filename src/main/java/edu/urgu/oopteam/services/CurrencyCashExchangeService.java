package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CashExchangeRate;
import edu.urgu.oopteam.crud.repository.CashExchangeRateRepository;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class CurrencyCashExchangeService implements ICurrencyCashExchangeService {
    private static final Logger LOGGER = Logger.getLogger(CurrencyCashExchangeService.class);
    private static final String SERVICE_ADDRESS = "https://banki.ru/products/currency/best_rates_summary/bank/";
    private static final int FETCH_RATE = 30;
    private static final Hashtable<Pair<String, String>, CompletableFuture<CashExchangeRate>> cachedExchangeRequests = new Hashtable<>();
    private final CashExchangeRateRepository cashExchangeRateRepository;


    @Autowired
    public CurrencyCashExchangeService(CashExchangeRateRepository cashExchangeRateRepository) {
        this.cashExchangeRateRepository = cashExchangeRateRepository;
    }

    /**
     * Check if date of last update is still actual
     *
     * @param rate currency exchange rate
     * @return true if actual, false if not
     */
    private static boolean isRateActual(CashExchangeRate rate) {
        var timeUnit = TimeUnit.MINUTES;
        var diffInMillies = (new Date()).getTime() - rate.getFetchTime().getTime();
        var diffInMinutes = timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);

        return diffInMinutes < FETCH_RATE;
    }

    @Override
    public CashExchangeRate getCashExchangeRate(String currencyCode, String city) throws ExecutionException, InterruptedException {
        synchronized (cachedExchangeRequests) {
            var activeFetch = cachedExchangeRequests.get(Pair.of(currencyCode, city));
            if (activeFetch != null) {
                return activeFetch.get();
            }

            var fetchRequest = CompletableFuture.supplyAsync(() -> {
                try {
                    var rate = cashExchangeRateRepository.getByCurrencyCodeAndCity(currencyCode, city);
                    // if exists and up-to-date, return it
                    return rate == null
                            ? createCashExchangeRate(currencyCode, city)
                            : isRateActual(rate) ? rate : updateCashExchangeRate(rate);
                } catch (IOException e) {
                    LOGGER.error(e);
                    return null;
                }
            });
            cachedExchangeRequests.put(Pair.of(currencyCode, city), fetchRequest);
            return fetchRequest.get();
        }
    }

    private CashExchangeRate createCashExchangeRate(String currencyCode, String city) throws IOException {
        var exchangeRate = fetchExchangeValues(currencyCode, city);

        var rate = new CashExchangeRate(currencyCode, city, exchangeRate.buyData.rate, exchangeRate.buyData.bankName,
                exchangeRate.sellData.rate, exchangeRate.sellData.bankName, new Date());
        cashExchangeRateRepository.save(rate);
        return rate;
    }

    private CashExchangeRate updateCashExchangeRate(CashExchangeRate cashExchangeRate) throws IOException {
        var updatedRate = fetchExchangeValues(cashExchangeRate.getCurrencyCode(), cashExchangeRate.getCity());

        cashExchangeRate.setBuyRate(updatedRate.buyData.rate);
        cashExchangeRate.setBuyBankName(updatedRate.buyData.bankName);
        cashExchangeRate.setSellRate(updatedRate.sellData.rate);
        cashExchangeRate.setSellBankName(updatedRate.sellData.bankName);
        cashExchangeRate.setFetchTime(new Date());

        cashExchangeRateRepository.save(cashExchangeRate);
        return cashExchangeRate;
    }

    private ResponseExchangeValue fetchExchangeValues(String currencyCode, String city) throws IOException {
        var response = WebService.getPageAsString(getRequestAddress(currencyCode, city), "UTF-8",
                getRequestHeaders());

        var page = Jsoup.parse(response);
        var exchangeData = page.select("td.currency-table__bordered-col");
        var buyData = extractExchangeDate(exchangeData.first());
        var sellData = extractExchangeDate(exchangeData.last());

        return new ResponseExchangeValue(buyData, sellData);
    }

    private ExchangeData extractExchangeDate(Element element) {
        var rate = Double.parseDouble(element
                .select("div.currency-table__large-text")
                .first()
                .html()
                .replace(',', '.'));
        var bankName = element.select("div.currency-table__rate__text").first().html().strip();
        return new ExchangeData(bankName, rate);
    }

    private String getRequestAddress(String currencyCode, String city) {
        return SERVICE_ADDRESS + currencyCode + "/" + city + "/";
    }

    private List<Pair<String, String>> getRequestHeaders() {
        return List.of(
                Pair.of("Cookie", "BANKI_RU_GUEST_ID=693599905; BANKI_RU_USER_IDENTITY_UID=2554564999135270847;"),
                Pair.of("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0"),
                Pair.of("X-Requested-With", "XMLHttpRequest"));
    }


    // UTILITY CLASSES

    private class ResponseExchangeValue {
        ExchangeData buyData;
        ExchangeData sellData;

        ResponseExchangeValue(ExchangeData buyData, ExchangeData sellData) {
            this.buyData = buyData;
            this.sellData = sellData;
        }
    }

    private class ExchangeData {
        String bankName;
        double rate;

        ExchangeData(String bankName, double rate) {
            this.bankName = bankName;
            this.rate = rate;
        }
    }
}