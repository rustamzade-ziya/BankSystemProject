package com.bank.banksystem.dto.request;

import java.math.BigDecimal;

public class CashbackTransferRequest {
    private Long userId;
    private Long targetCardId;
    private BigDecimal amount;

    public CashbackTransferRequest() {
    }

    public CashbackTransferRequest(Long userId, Long targetCardId, BigDecimal amount) {
        this.userId = userId;
        this.targetCardId = targetCardId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTargetCardId() {
        return targetCardId;
    }

    public void setTargetCardId(Long targetCardId) {
        this.targetCardId = targetCardId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
