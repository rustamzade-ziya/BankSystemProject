package com.bank.banksystem.dto.request;

import java.math.BigDecimal;

public class CreateCreditCardRequest {
    private Long userId;
    private String ppn;
    private String currency;
    private BigDecimal loanAmount;
    private String pin;

    public CreateCreditCardRequest() {
    }

    public CreateCreditCardRequest(Long userId, String ppn, String currency, BigDecimal loanAmount,  String pin) {
        this.userId = userId;
        this.ppn = ppn;
        this.currency = currency;
        this.loanAmount = loanAmount;
        this.pin = pin;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPpn() {
        return ppn;
    }

    public void setPpn(String ppn) {
        this.ppn = ppn;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getPin() {
        return pin;
    }
    public void setPin(String pin) {
        this.pin = pin;
    }
}