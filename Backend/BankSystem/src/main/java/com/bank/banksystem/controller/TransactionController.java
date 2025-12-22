package com.bank.banksystem.controller;


import com.bank.banksystem.dto.response.TransactionResponse;
import com.bank.banksystem.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/auth/login/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/recent/{userId}")
    public ResponseEntity<List<TransactionResponse>> getRecentTransactions(@PathVariable Long userId) {
        return ResponseEntity.ok(transactionService.getRecentTransactions(userId));
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<List<TransactionResponse>> getCardTransactions(@PathVariable Long cardId) {
        return ResponseEntity.ok(transactionService.getCardTransactions(cardId));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionDetails(@PathVariable Long transactionId) {
        return ResponseEntity.ok(transactionService.getTransactionDetails(transactionId));
    }
}