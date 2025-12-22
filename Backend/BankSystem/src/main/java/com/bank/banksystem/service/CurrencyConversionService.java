package com.bank.banksystem.service;

import com.bank.banksystem.entity.CurrencyExchangeRate;
import com.bank.banksystem.repository.CurrencyExchangeRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CurrencyConversionService {

    private final CurrencyExchangeRateRepository rateRepository;

    public CurrencyConversionService(CurrencyExchangeRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public BigDecimal convert(
            BigDecimal amount,
            String fromCurrency,
            String toCurrency
    ) {

        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }

        CurrencyExchangeRate rate = rateRepository
                .findRate(fromCurrency, toCurrency)
                .orElseThrow(() ->
                        new RuntimeException("Exchange rate not found"));

        return amount
                .multiply(rate.getRate())
                .setScale(2, RoundingMode.HALF_UP);
    }
}
