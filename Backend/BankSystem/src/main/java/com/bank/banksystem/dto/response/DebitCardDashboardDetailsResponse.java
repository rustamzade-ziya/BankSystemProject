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
public class DebitCardDashboardDetailsResponse {

    private Long cardId;

    private BigDecimal balance;

    private String ppn;

    private String currency;

    private String cvv;

    private String expiry_date;

    private String status;
}
