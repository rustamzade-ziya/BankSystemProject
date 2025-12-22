package com.bank.banksystem.dto.response;

public class CreateDebitCardResponse {
    private Long cardId;
    private String cvv;
    private String expiryDate;
    private String status;
    private String message;

    public CreateDebitCardResponse() {
    }

    public CreateDebitCardResponse(Long cardId, String cvv, String expiryDate, String status, String message) {
        this.cardId = cardId;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
        this.status = status;
        this.message = message;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}