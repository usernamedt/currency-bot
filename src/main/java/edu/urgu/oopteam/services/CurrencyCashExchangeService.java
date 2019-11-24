package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CashExchangeRate;
import edu.urgu.oopteam.crud.repository.CashExchangeRateRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CurrencyCashExchangeService implements ICurrencyCashExchangeService {
    private static final String SERVICE_ADDRESS = "https://banki.ru/products/currency/best_rates_summary/bank/";
    private static final int FETCH_RATE = 30;
    private final CashExchangeRateRepository cashExchangeRateRepository;

    @Autowired
    public CurrencyCashExchangeService(CashExchangeRateRepository cashExchangeRateRepository) {
        this.cashExchangeRateRepository = cashExchangeRateRepository;
    }


    @Override
    public CashExchangeRate getCashExchangeRate(String currencyCode, String city) throws SQLException, IOException {
        var rate = cashExchangeRateRepository.getByCurrencyCodeAndCity(currencyCode, city);
        if (rate != null) {
            // if exists and up-to-date, return it
            if (getDateDiff(rate.getFetchTime(), new Date(), TimeUnit.MINUTES) > FETCH_RATE) {
                // else refresh and return
                updateCashExchangeRate(rate);
            }
            return rate;
        }
        return createCashExchangeRate(currencyCode, city);
    }


    private CashExchangeRate createCashExchangeRate(String currencyCode, String city) throws IOException {
        var exchangeRates = fetchExchangeValues(currencyCode, city);

        var rate = new CashExchangeRate(currencyCode,city, exchangeRates.buyData.rate, exchangeRates.buyData.bankName,
                exchangeRates.sellData.rate, exchangeRates.sellData.bankName, new Date());
        cashExchangeRateRepository.save(rate);
        return rate;
    }


    private void updateCashExchangeRate(CashExchangeRate cashExchangeRate) throws IOException {
        var updatedRates = fetchExchangeValues(cashExchangeRate.getCurrencyCode(), cashExchangeRate.getCity());

        cashExchangeRate.setBuyRate(updatedRates.buyData.rate);
        cashExchangeRate.setBuyBankName(updatedRates.buyData.bankName);
        cashExchangeRate.setSellRate(updatedRates.sellData.rate);
        cashExchangeRate.setSellBankName(updatedRates.sellData.bankName);
        cashExchangeRate.setFetchTime(new Date());

        cashExchangeRateRepository.save(cashExchangeRate);
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
                .replace(',','.'));
        var bankName = element.select("div.currency-table__rate__text").first().html().strip();
        return new ExchangeData(bankName, rate);
    }


    private String getRequestAddress(String currencyCode, String city) {
        return SERVICE_ADDRESS + currencyCode + "/" + city + "/";
    }

    private List<Pair<String, String>> getRequestHeaders() {
        return List.of(Pair.of("Cookie", "BANKI_RU_GUEST_ID=693599905; BANKI_RU_USER_IDENTITY_UID=2554564999135270847;"),
                Pair.of("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:70.0) Gecko/20100101 Firefox/70.0"),
                Pair.of("X-Requested-With", "XMLHttpRequest"));
    }

    /**
     * Get a diff between two dates
     *
     * @param date1    the oldest date
     * @param date2    the newest date
     * @param timeUnit the unit in which you want the diff
     * @return the diff value, in the provided unit
     */
    private static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }


    // UTILITY CLASSES

    private class ResponseExchangeValue {
        ResponseExchangeValue(ExchangeData buyData, ExchangeData sellData) {
            this.buyData = buyData;
            this.sellData = sellData;
        }

        ExchangeData buyData;
        ExchangeData sellData;
    }

    private class ExchangeData {
        ExchangeData(String bankName, double rate) {
            this.bankName = bankName;
            this.rate = rate;
        }

        String bankName;
        double rate;
    }
}
