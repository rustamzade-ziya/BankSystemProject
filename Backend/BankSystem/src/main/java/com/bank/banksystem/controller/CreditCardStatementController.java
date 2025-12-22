package com.bank.banksystem.controller;

import com.bank.banksystem.entity.CreditCardStatement;
import com.bank.banksystem.service.CreditCardStatementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/credit-card-statements")
public class CreditCardStatementController {

    private final CreditCardStatementService creditCardStatementService;

    public CreditCardStatementController(CreditCardStatementService creditCardStatementService) {
        this.creditCardStatementService = creditCardStatementService;
    }

    @GetMapping("/{cardId}/latest")
    public CreditCardStatement latest(@PathVariable Long cardId) {
        return creditCardStatementService.getLatestStatement(cardId)
                .orElseThrow(() -> new IllegalArgumentException("No statements for cardId=" + cardId));
    }

    @GetMapping("/{cardId}")
    public List<CreditCardStatement> all(@PathVariable Long cardId) {
        return creditCardStatementService.getAllStatements(cardId);
    }


    // Manual job trigger for STATEMENT DATE for testing (Postman)
    @PostMapping("/jobs/finalize")
    public String finalizeJob(@RequestParam(required = false) String date) {
        LocalDate d = (date == null) ? LocalDate.now() : LocalDate.parse(date);
        int processed = creditCardStatementService.finalizeOpenStatements(d);
        return "finalized=" + processed;
    }

    // Manual job trigger for DUE DATE for testing (Postman)
    @PostMapping("/jobs/due")
    public String dueJob(@RequestParam(required = false) String date) {
        LocalDate d = (date == null) ? LocalDate.now() : LocalDate.parse(date);
        int processed = creditCardStatementService.processDueDates(d);
        return "due_processed=" + processed;
    }

    // Manual job trigger for ADDING INTEREST FOR 1 DAY for testing (Postman)
    //http://localhost:8080/api/credit-card-statements/jobs/interest?date=2026-01-25
    @PostMapping("/jobs/interest")
    public String interestJob(@RequestParam(required = false) String date) {
        LocalDate d = (date == null) ? LocalDate.now() : LocalDate.parse(date);
        int processed = creditCardStatementService.accrueDailyInterest(d);
        return "interest_posted=" + processed;
    }


}
