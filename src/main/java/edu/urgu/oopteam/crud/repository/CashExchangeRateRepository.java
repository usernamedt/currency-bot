package edu.urgu.oopteam.crud.repository;

import edu.urgu.oopteam.crud.model.CashExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashExchangeRateRepository extends JpaRepository<CashExchangeRate, Long> {
    CashExchangeRate getByCurrencyCodeAndCity(String currencyCode, String city);

    @Override
    void deleteAll();
}
