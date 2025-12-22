package com.bank.banksystem.controller;

import com.bank.banksystem.dto.response.CashbackResponse;
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
            CashbackResponse response = new CashbackResponse(
                    userId,
                    balance,
                    "Cashback balance retrieved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            CashbackResponse errorResponse = new CashbackResponse(
                    userId,
                    BigDecimal.ZERO,
                    "Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
