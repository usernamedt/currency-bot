package edu.urgu.oopteam.crud.repository;

import edu.urgu.oopteam.crud.model.CashExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;

@Repository
public interface CashExchangeRateRepository extends JpaRepository<CashExchangeRate, Long> {
    CashExchangeRate getByCurrencyCodeAndCity(String currencyCode, String city);
}
