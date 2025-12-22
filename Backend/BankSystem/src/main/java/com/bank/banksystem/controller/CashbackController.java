package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.CashbackPaymentRequest;
import com.bank.banksystem.dto.request.CashbackTransferRequest;
import com.bank.banksystem.dto.response.CashbackResponse;
import com.bank.banksystem.dto.response.CashbackTransferResponse;
import com.bank.banksystem.service.CashbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/cashback")
public class CashbackController {

    @Autowired
    private CashbackService cashbackService;

    @GetMapping("/{userId}")
    public ResponseEntity<CashbackResponse> getCashbackBalance(@PathVariable Long userId) {
        try {
            BigDecimal balance = cashbackService.getCashbackBalance(userId);
            CashbackResponse response = new CashbackResponse(userId, balance,
                    "Cashback balance retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CashbackResponse errorResponse = new CashbackResponse(userId, BigDecimal.ZERO, "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<CashbackTransferResponse> transferToCard(@RequestBody CashbackTransferRequest request) {
        try {
            String message = cashbackService.transferToCard(request.getUserId(), request.getTargetCardId(),
                    request.getAmount());
            BigDecimal remainingBalance = cashbackService.getCashbackBalance(request.getUserId());
            CashbackTransferResponse response = new CashbackTransferResponse(true, message, remainingBalance);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CashbackTransferResponse errorResponse = new CashbackTransferResponse(false, "Error: " + e.getMessage(),
                    BigDecimal.ZERO);
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/pay")
    public ResponseEntity<String> payWithCashback(@RequestBody CashbackPaymentRequest request) {
        try {
            String message = cashbackService.payWithCashback(request.getUserId(), request.getAmount(),
                    request.getServiceType());
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
