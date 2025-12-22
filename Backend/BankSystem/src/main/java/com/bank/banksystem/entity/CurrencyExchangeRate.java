package com.bank.banksystem.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "currency_exchange_rate",
        uniqueConstraints = @UniqueConstraint(columnNames = {"currency_from", "currency_to"}))
public class CurrencyExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rate_id;

    private String currency_from;
    private String currency_to;

    private BigDecimal rate;

    public Long getRate_id() {
        return rate_id;
    }

    public String getCurrency_from() {
        return currency_from;
    }

    public String getCurrency_to() {
        return currency_to;
    }

    public BigDecimal getRate() {
        return rate;
    }
}
