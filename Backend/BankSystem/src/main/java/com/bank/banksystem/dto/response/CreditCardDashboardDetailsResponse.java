package com.bank.banksystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class CreditCardDashboardDetailsResponse {

    private String cardId;

    private BigDecimal balance;

    private String ppn;

    private String currency;

    private String expiryDate;

    private String status;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;
}
