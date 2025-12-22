package com.bank.banksystem.dto.response;

import lombok.Data;

@Data
public class TransferResponse {
    private boolean success;
    private String message;
    private Long transactionId;
}