package com.bank.banksystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCreditCardResponse {
    private Long cardId;
    private String cvv;
    private String expiryDate;
    private String status;
    private BigDecimal interestRate;
    private BigDecimal loanAmount;
    private String message;
}
