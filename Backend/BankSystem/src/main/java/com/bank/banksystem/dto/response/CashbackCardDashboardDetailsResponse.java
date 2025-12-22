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
public class CashbackCardDashboardDetailsResponse {
    private Long cardId;

    private BigDecimal balance;

    private BigDecimal cashbackPercent;
}
