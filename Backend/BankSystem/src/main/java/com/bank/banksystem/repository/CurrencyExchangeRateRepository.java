package com.bank.banksystem.repository;

import com.bank.banksystem.entity.CurrencyExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CurrencyExchangeRateRepository
        extends JpaRepository<CurrencyExchangeRate, Long> {

    @Query(
            value = """
            SELECT * FROM currency_exchange_rate
            WHERE currency_from = :from AND currency_to = :to
            """,
            nativeQuery = true
    )
    Optional<CurrencyExchangeRate> findRate(
            @Param("from") String from,
            @Param("to") String to
    );
}
