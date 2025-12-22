package com.bank.banksystem.dto.response;

import java.math.BigDecimal;

public class CashbackTransferResponse {
    private boolean success;
    private String message;
    private BigDecimal remainingBalance;

    public CashbackTransferResponse() {
    }

    public CashbackTransferResponse(boolean success, String message, BigDecimal remainingBalance) {
        this.success = success;
        this.message = message;
        this.remainingBalance = remainingBalance;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(BigDecimal remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
}
