package com.bank.banksystem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private Long id;

    private Long senderId;

    private Long receiverId;

    private String type;

    private BigDecimal amount;

    private BigDecimal convertedAmount;   // ✅ added

    private String senderCurrency;       // ✅ added

    private String receiverCurrency;     // ✅ added

    private LocalDateTime date;

}
