package edu.urgu.oopteam.services;

import edu.urgu.oopteam.crud.model.CashExchangeRate;
import edu.urgu.oopteam.crud.model.CurrencyTrackRequest;
import edu.urgu.oopteam.crud.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ICurrencyCashExchangeService {
    CashExchangeRate getCashExchangeRate(String currencyCode, String city) throws SQLException, IOException, ExecutionException, InterruptedException;
}
