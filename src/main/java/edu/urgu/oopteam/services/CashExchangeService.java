package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CashExchangeRate;
import edu.urgu.oopteam.crud.repository.CashExchangeRateRepository;
import edu.urgu.oopteam.viewmodels.BuySellExchangeRates;
import edu.urgu.oopteam.viewmodels.ExchangeData;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class CashExchangeService implements ICashExchangeService {
    private static final Logger LOGGER = Logger.getLogger(CashExchangeService.class);
    private static final String SERVICE_ADDRESS = "https://banki.ru/products/currency/best_rates_summary/bank/";
    private static final int FETCH_RATE = 30;
    private static final ConcurrentHashMap<Pair<String, String>, CompletableFuture<CashExchangeRate>> cachedExchangeRequests = new ConcurrentHashMap<>();
    private final CashExchangeRateRepository cashExchangeRateRepository;
    private final WebService webService;


    @Autowired
    public CashExchangeService(CashExchangeRateRepository cashExchangeRateRepository, WebService webService) {
        this.cashExchangeRateRepository = cashExchangeRateRepository;
        this.webService = webService;
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
        CompletableFuture<CashExchangeRate> fetchRequest;
        var activeFetch = cachedExchangeRequests.get(Pair.of(currencyCode, city));
        if (activeFetch != null && (!activeFetch.isDone() || isRateActual(activeFetch.get()))) {
            fetchRequest = activeFetch;
        } else {
            fetchRequest = CompletableFuture.supplyAsync(() -> {
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
        }

        return fetchRequest.get();
    }

    /**
     * Create CashExchangeRate entry in database
     *
     * @param currencyCode code of the currency
     * @param city         city name
     * @return CashExchangeRate - created entity
     * @throws IOException exception if call to external web resource failed
     */
    private CashExchangeRate createCashExchangeRate(String currencyCode, String city) throws IOException {
        var exchangeRate = fetchExchangeValues(currencyCode, city);

        var rate = new CashExchangeRate(currencyCode, city, exchangeRate.getBuyData().getRate(),
                exchangeRate.getBuyData().getBankName(),
                exchangeRate.getSellData().getRate(), exchangeRate.getSellData().getBankName(), new Date());
        cashExchangeRateRepository.save(rate);
        return rate;
    }

    /**
     * Update existing CashExchangeRate with new values from database
     *
     * @param cashExchangeRate entity to update
     * @return CashExchangeRate - updated with actual data
     * @throws IOException exception if call to external web resource failed
     */
    private CashExchangeRate updateCashExchangeRate(CashExchangeRate cashExchangeRate) throws IOException {
        var updatedRate = fetchExchangeValues(cashExchangeRate.getCurrencyCode(), cashExchangeRate.getCity());

        cashExchangeRate.setBuyRate(updatedRate.getBuyData().getRate());
        cashExchangeRate.setBuyBankName(updatedRate.getBuyData().getBankName());
        cashExchangeRate.setSellRate(updatedRate.getSellData().getRate());
        cashExchangeRate.setSellBankName(updatedRate.getSellData().getBankName());
        cashExchangeRate.setFetchTime(new Date());

        cashExchangeRateRepository.save(cashExchangeRate);
        return cashExchangeRate;
    }

    /**
     * Perform a request to external web resource to get updated exchange values
     *
     * @param currencyCode code of the currency
     * @param city         city name
     * @return BuySellExchangeRates - fresh exchange buy/sell values
     * @throws IOException exception if call to external web resource failed
     */
    private BuySellExchangeRates fetchExchangeValues(String currencyCode, String city) throws IOException {
        var response = webService.getPageAsString(getRequestAddress(currencyCode, city), "UTF-8",
                getRequestHeaders());

        var page = Jsoup.parse(response);
        var exchangeData = page.select("td.currency-table__bordered-col");
        var buyData = extractExchangeData(exchangeData.first());
        var sellData = extractExchangeData(exchangeData.last());

        return new BuySellExchangeRates(buyData, sellData);
    }

    /**
     * Parse exchange data from provided JSoup element
     *
     * @param element JSoup element
     * @return ExchangeData - parsed exchange data
     */
    private ExchangeData extractExchangeData(Element element) {
        var rate = new BigDecimal(element
                .select("div.currency-table__large-text")
                .first()
                .html()
                .replace(',', '.'));
        var bankName = element.select("div.currency-table__rate__text").first().html().strip();
        return new ExchangeData(bankName, rate);
    }

    /**
     * Form request URL address to receive data for provided city and currency
     *
     * @param currencyCode code of the currency
     * @param city         city name
     * @return URL address of request with specified city and currency
     */
    private String getRequestAddress(String currencyCode, String city) {
        return SERVICE_ADDRESS + currencyCode + "/" + city + "/";
    }

    /**
     * Get request headers to pass the external web source checks
     *
     * @return pairs of request headers
     */
    private List<Pair<String, String>> getRequestHeaders() {
        return List.of(
                Pair.of("Cookie", "BANKI_RU_GUEST_ID=693599905; BANKI_RU_USER_IDENTITY_UID=2554564999135270847;"),
                Pair.of("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0"),
                Pair.of("X-Requested-With", "XMLHttpRequest"));
    }
}