// src/main/java/com/bank/banksystem/controller/CreditCardRepayTestController.java
package com.bank.banksystem.controller;

import com.bank.banksystem.service.CreditCardStatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/test/credit-card")
public class CreditCardRepayTestController {

    private final CreditCardStatementService creditCardStatementService;

    public CreditCardRepayTestController(CreditCardStatementService creditCardStatementService) {
        this.creditCardStatementService = creditCardStatementService;
    }


    //{
    //  "cardId": 7630706331320469,
    //  "amount": 100.08,
    //  "businessDate": "2026-02-15"
    //}

    /**
     * TEST endpoint (does NOT change TransferRequest and does NOT affect production transfer logic).
     *
     * Allows you to:
     * - post a repayment to a credit card
     * - optionally set businessDate to insert a transaction with that date
     *
     * This is needed because jobs rely on transaction dates and they are local. This allows to imitate repayment at certain dates
     */
    @PostMapping("/repay")
    public ResponseEntity<?> repay(@RequestBody TestRepayRequest request) {
        try {
            creditCardStatementService.repay(request.getCardId(), request.getAmount(), request.getBusinessDate());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Repayment posted successfully"
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", ex.getMessage()
            ));
        }
    }

    public static class TestRepayRequest {
        private Long cardId;
        private BigDecimal amount;
        private LocalDate businessDate; // optional (YYYY-MM-DD)

        public Long getCardId() {
            return cardId;
        }

        public void setCardId(Long cardId) {
            this.cardId = cardId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public LocalDate getBusinessDate() {
            return businessDate;
        }

        public void setBusinessDate(LocalDate businessDate) {
            this.businessDate = businessDate;
        }
    }
}
