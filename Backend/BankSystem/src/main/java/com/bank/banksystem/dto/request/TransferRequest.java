package com.bank.banksystem.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    private Long senderAccountNumber;
    private Long receiverAccountNumber;
    private BigDecimal amount; // Düzəliş
    private String description;
}