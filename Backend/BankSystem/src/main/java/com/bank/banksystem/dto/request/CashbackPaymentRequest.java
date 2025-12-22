package com.bank.banksystem.dto.request;

import java.math.BigDecimal;

public class CashbackPaymentRequest {
    private Long userId;
    private BigDecimal amount;
    private String serviceType;
    private String receiverCode;

    public CashbackPaymentRequest() {
    }

    public CashbackPaymentRequest(Long userId, BigDecimal amount, String serviceType, String receiverCode) {
        this.userId = userId;
        this.amount = amount;
        this.serviceType = serviceType;
        this.receiverCode = receiverCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getReceiverCode() {
        return receiverCode;
    }

    public void setReceiverCode(String receiverCode) {
        this.receiverCode = receiverCode;
    }
}
