package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CashExchangeRate;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public interface ICashExchangeService {
    /**
     * Get CashExchangeRate for provided city and currency
     *
     * @param currencyCode currency code
     * @param city         city name
     * @return CashExchangeRate
     */
    CashExchangeRate getCashExchangeRate(String currencyCode, String city) throws SQLException, IOException, ExecutionException, InterruptedException;
}
