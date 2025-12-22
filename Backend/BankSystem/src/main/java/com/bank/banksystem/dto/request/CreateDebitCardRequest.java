package com.bank.banksystem.dto.request;

public class CreateDebitCardRequest {
    private Long userId;
    private String ppn;
    private String currency;
    private String pin;

    public CreateDebitCardRequest() {
    }

    public CreateDebitCardRequest(Long userId, String ppn, String currency, String pin) {
        this.userId = userId;
        this.ppn = ppn;
        this.currency = currency;
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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}