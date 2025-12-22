package com.bank.banksystem.scheduler;

import com.bank.banksystem.service.CreditCardStatementService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CreditCardStatementScheduler {

    private final CreditCardStatementService creditCardStatementService;

    public CreditCardStatementScheduler(CreditCardStatementService creditCardStatementService) {
        this.creditCardStatementService = creditCardStatementService;
    }

    // Every day 00:05
    @Scheduled(cron = "0 5 0 * * *")
    public void runDailyStatements() {
        LocalDate today = LocalDate.now();

        // 1) Close statements whose statement_date is reached
        creditCardStatementService.finalizeOpenStatements(today);

        // 2) Process due dates for billed statements
        creditCardStatementService.processDueDates(today);

        // 3) Interest accrual for overdue cards
        creditCardStatementService.accrueDailyInterest(today);
    }
}
