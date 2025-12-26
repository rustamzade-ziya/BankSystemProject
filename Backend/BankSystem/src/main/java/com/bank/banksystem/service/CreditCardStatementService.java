// src/main/java/com/bank/banksystem/service/CreditCardStatementService.java
package com.bank.banksystem.service;

import com.bank.banksystem.entity.CreditCard;
import com.bank.banksystem.entity.CreditCardStatement;
import com.bank.banksystem.repository.CreditCardRepository;
import com.bank.banksystem.repository.CreditCardStatementRepository;
import com.bank.banksystem.repository.TransactionHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreditCardStatementService {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardStatementRepository statementRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionService transactionService;

    // ===== Cycle configuration (MVP) =====
    private static final int STATEMENT_PERIOD_DAYS = 20;
    private static final int GRACE_PERIOD_DAYS = 10;

    private static final BigDecimal MIN_PAYMENT_RATE = new BigDecimal("0.1"); // 5%

    // LATE_FEE_AMOUNT is PERCENT (20 => 20% of current debt)
    private static final BigDecimal LATE_FEE_AMOUNT = new BigDecimal("20");

    // ===== Transaction types (MVP) =====
    private static final String TYPE_INTEREST = "CREDIT_CARD_INTEREST";
    private static final String TYPE_LATE_FEE = "CREDIT_CARD_LATE_FEE";

    // Any outgoing CREDIT transfer/spend from credit card (purchase-like)
    private static final String TYPE_CREDIT_SPEND_LIKE = "CREDIT_TO_%";

    // Incoming payments that should reduce debt (repayment-like)
    private static final List<String> PAYMENT_TYPES = List.of(
            "DEBIT_TO_CREDIT_INTERNAL",
            "DEBIT_TO_CREDIT_TEST",
            "CREDIT_TO_CREDIT_WITH_FEE"
    );

    public CreditCardStatementService(CreditCardRepository creditCardRepository,
                                      CreditCardStatementRepository statementRepository,
                                      TransactionHistoryRepository transactionHistoryRepository,
                                      TransactionService transactionService) {
        this.creditCardRepository = creditCardRepository;
        this.statementRepository = statementRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.transactionService = transactionService;
    }

    // ===== Read APIs (used by controller) =====

    public List<CreditCardStatement> getAllStatements(Long cardId) {
        if (cardId == null) return List.of();
        return statementRepository.findAllStatementsByCardIdOrderByDateDesc(cardId);
    }

    public List<CreditCardStatement> getAllPayableStatements(Long cardId) {
        if (cardId == null) return List.of();
        return statementRepository.findPayableStatementsByCardIdOrderByDueDateAsc(cardId);
    }

    public java.util.Optional<CreditCardStatement> getLatestStatement(Long cardId) {
        if (cardId == null) return java.util.Optional.empty();
        return statementRepository.findLatestStatementByCardId(cardId);
    }

    /**
     * Ensures an OPEN statement exists (used when card starts being used).
     */
    @Transactional
    public CreditCardStatement bootstrapIfMissing(Long cardId) {
        CreditCard card = creditCardRepository.findByCardId(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Credit card not found: " + cardId));

        return statementRepository.findLatestOpenStatementByCardId(cardId)
                .orElseGet(() -> createOpenStatement(card, LocalDate.now()));
    }

    /**
     * Same as bootstrapIfMissing, but takes the card instance (used by TransferService).
     */
    @Transactional
    public CreditCardStatement ensureActiveStatementExists(CreditCard card) {
        return statementRepository.findLatestOpenStatementByCardId(card.getCardId())
                .orElseGet(() -> createOpenStatement(card, LocalDate.now()));
    }

    // ===== Jobs =====

    /**
     * Close OPEN statements whose statement_date is reached.
     * - We calculate and FIX totals on statementDate and set status = BILLED.
     * - We do NOT touch this statement again later (except:
     * - at dueDate we set only status + closingBalance,
     * - if total debt becomes 0 we can flip old payable statements to PAID).
     * - New purchases/payments after statementDate must go into the NEW OPEN statement.
     */
    @Transactional
    public int finalizeOpenStatements(LocalDate today) {
        var openStatements = statementRepository.findOpenStatementsToFinalize(today);
        int processed = 0;

        for (CreditCardStatement st : openStatements) {
            Long cardId = st.getCardId();

            CreditCard card = creditCardRepository.findByCardId(cardId)
                    .orElseThrow(() -> new IllegalArgumentException("Credit card not found: " + cardId));

            normalizeCreditCardAccounting(card);

            LocalDate cycleStart = st.getStatementDate().minusDays(STATEMENT_PERIOD_DAYS);
            LocalDate cycleEnd = st.getStatementDate();

            Totals totals = calculateTotals(cardId, cycleStart, cycleEnd);

            // Snapshot at statementDate: balance == available credit at this moment
            st.setOpeningBalance(scaleMoney(safe(card.getBalance())));

            // Save period totals
            st.setPurchases(totals.purchases);
            st.setPayments(totals.payments);

            // IMPORTANT: do NOT allow fees/interest to "drop to 0" by overwriting.
            // Keep the larger (already accumulated) value if repository sums return 0 / smaller.
            BigDecimal mergedFees = maxMoney(st.getFeesCharged(), totals.fees);
            BigDecimal mergedInterest = maxMoney(st.getInterestCharged(), totals.interest);

            st.setFeesCharged(mergedFees);
            st.setInterestCharged(mergedInterest);

            // On BILLED statement we do NOT store closingBalance (it will be filled only at dueDate)
            st.setClosingBalance(null);

            // Due = debt at statementDate (limit - available)
            BigDecimal totalDue = scaleMoney(safe(card.getCurrentDebt()));
            st.setTotalPaymentDue(totalDue);
            st.setMinPaymentDue(scaleMoney(totalDue.multiply(MIN_PAYMENT_RATE)));

            st.setStatus("BILLED");
            statementRepository.save(st);

            processed++;

            // Create next OPEN statement starting next day
            createNextOpenStatementIfMissing(card, cycleEnd.plusDays(1));
        }

        return processed;
    }

    /**
     * Process dueDate for BILLED statements.
     * <p>
     * We do ONE-TIME update at dueDate:
     * - closingBalance = current card balance (available credit) at dueDate, to "freeze" it
     * - status:
     * PAID if gracePayments >= totalPaymentDue
     * OVERDUE_MIN_PAID if gracePayments >= minPaymentDue
     * OVERDUE otherwise (+ post late fee to card debt, but late fee goes to NEW OPEN statement)
     */
    @Transactional
    public int processDueDates(LocalDate today) {
        var billed = statementRepository.findBilledStatementsToProcessDue(today);
        int processed = 0;

        for (CreditCardStatement st : billed) {
            Long cardId = st.getCardId();

            CreditCard card = creditCardRepository.findByCardId(cardId)
                    .orElseThrow(() -> new IllegalArgumentException("Credit card not found: " + cardId));

            normalizeCreditCardAccounting(card);

            BigDecimal totalDue = scaleMoney(safe(st.getTotalPaymentDue()));
            BigDecimal minDue = scaleMoney(safe(st.getMinPaymentDue()));

            // Grace period payments: (statementDate + 1) .. dueDate (inclusive)
            BigDecimal gracePayments = sumPayments(cardId, st.getStatementDate().plusDays(1), st.getDueDate());

            // Freeze closing balance at dueDate (available credit)
            st.setClosingBalance(scaleMoney(safe(card.getBalance())));

            if (totalDue.compareTo(BigDecimal.ZERO) <= 0 || gracePayments.compareTo(totalDue) >= 0) {
                st.setStatus("PAID");
            } else if (gracePayments.compareTo(minDue) >= 0) {
                st.setStatus("OVERDUE_MIN_PAID");
            } else {
                st.setStatus("OVERDUE");

                // Late fee is posted to card + latest OPEN statement (NOT to this old BILLED one)
                postLateFee(cardId, st.getUserId(), today);
            }

            statementRepository.save(st);
            processed++;
        }

        return processed;
    }

    /**
     * Accrue daily interest for cards that have ANY overdue-like statement.
     * <p>
     * IMPORTANT RULE (your new model):
     * - Do NOT add interest into the old overdue statement (it stays fixed).
     * - Interest increases card currentDebt and is recorded into the latest OPEN statement.
     * <p>
     * Also idempotent by date:
     * - if interest transaction for today already exists -> skip.
     */
    @Transactional
    public int accrueDailyInterest(LocalDate today) {
        var overdueStatements = statementRepository.findAllOverdueLikeStatements();

        Set<Long> overdueCardIds = overdueStatements.stream()
                .map(CreditCardStatement::getCardId)
                .collect(Collectors.toSet());

        int processed = 0;

        for (Long cardId : overdueCardIds) {
            CreditCard card = creditCardRepository.findByCardId(cardId).orElse(null);
            if (card == null) continue;

            normalizeCreditCardAccounting(card);

            // Idempotency for the same day
            if (alreadyPosted(cardId, TYPE_INTEREST, today)) {
                continue;
            }

            BigDecimal debt = safe(card.getCurrentDebt());
            if (debt.compareTo(BigDecimal.ZERO) <= 0) {
                // If debt is already 0 -> also mark all payable statements as PAID (cleanup)
                statementRepository.markAllPayableStatementsPaid(cardId);
                continue;
            }

            // APR in percent (e.g. 20.00 = 20% APR). Fallback for MVP.
            BigDecimal apr = safe(card.getInterestRate());
            if (apr.compareTo(BigDecimal.ZERO) <= 0) {
                apr = new BigDecimal("20.00");
            }

            // daily interest = debt * (apr/100) / 365
            BigDecimal annualRate = apr.divide(new BigDecimal("100"), 12, RoundingMode.HALF_UP);
            BigDecimal interestRaw = debt.multiply(annualRate).divide(new BigDecimal("365"), 12, RoundingMode.HALF_UP);
            BigDecimal interest = interestRaw.setScale(2, RoundingMode.HALF_UP);

            // If rounding gives 0.00 but raw > 0 => charge minimal 0.01
            if (interest.compareTo(BigDecimal.ZERO) == 0 && interestRaw.compareTo(BigDecimal.ZERO) > 0) {
                interest = new BigDecimal("0.01");
            }

            if (interest.compareTo(BigDecimal.ZERO) <= 0) continue;

            // Apply interest to card
            BigDecimal newDebt = scaleMoney(debt.add(interest));
            card.setCurrentDebt(newDebt);

            BigDecimal limit = safe(card.getLoanAmount());
            BigDecimal available = limit.subtract(newDebt);
            if (available.compareTo(BigDecimal.ZERO) < 0) available = BigDecimal.ZERO;

            card.setBalance(scaleMoney(available));
            creditCardRepository.save(card);

            // Record transaction (date is "now" in TransactionService)
            transactionService.createTransaction(
                    cardId, card.getUser().getUser_id(),
                    cardId, card.getUser().getUser_id(),
                    TYPE_INTEREST,
                    interest,
                    null,
                    null,
                    null,
                    null
            );

            // Record into latest OPEN statement (this is where new charges go)
            CreditCardStatement open = bootstrapIfMissing(cardId);

            open.setInterestCharged(scaleMoney(safe(open.getInterestCharged()).add(interest)));

            // IMPORTANT: keep totalPaymentDue growing when interest accrues
            addToTotalPaymentDue(open, interest);

            statementRepository.save(open);

            processed++;
        }

        return processed;
    }

    // ===== Repayment sync =====

    /**
     * Called from TransferService after debit->credit transfer (repayment).
     * <p>
     * NEW RULE:
     * - Do NOT touch old BILLED/OVERDUE statements (otherwise chaos).
     * - Always write repayments into the LATEST OPEN statement.
     * - Only if the whole card debt is 0 -> we allow marking old payable statements as PAID.
     */
    @Transactional
    public void registerRepayment(Long cardId, BigDecimal amount, LocalDate paymentDate) {
        if (cardId == null || amount == null) return;

        BigDecimal pay = scaleMoney(amount);
        if (pay.compareTo(BigDecimal.ZERO) <= 0) return;

        CreditCard card = creditCardRepository.findByCardId(cardId).orElse(null);
        if (card == null) return;

        normalizeCreditCardAccounting(card);

        CreditCardStatement open = statementRepository.findLatestOpenStatementByCardId(cardId)
                .orElseGet(() -> createOpenStatement(card, LocalDate.now()));

        open.setPayments(scaleMoney(safe(open.getPayments()).add(pay)));

        // IMPORTANT: repayment should reduce totalPaymentDue (but not below 0)
        subtractFromTotalPaymentDue(open, pay);

        statementRepository.save(open);

        // If debt fully cleared -> mark all payable statements as PAID
        if (safe(card.getCurrentDebt()).compareTo(BigDecimal.ZERO) == 0) {
            statementRepository.markAllPayableStatementsPaid(cardId);
        }
    }

    /**
     * TEST helper (does NOT use TransferService).
     * <p>
     * - Updates card accounting (debt/balance)
     * - Inserts a transaction with a custom business date (so you can test jobs with dates)
     * - Syncs repayment into latest OPEN statement
     */
    @Transactional
    public void repay(Long cardId, BigDecimal amount, LocalDate businessDate) {
        if (cardId == null) throw new IllegalArgumentException("cardId is required");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("amount must be > 0");
        }

        CreditCard card = creditCardRepository.findByCardId(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Credit card not found: " + cardId));

        normalizeCreditCardAccounting(card);

        // Validate custom business date rules (only for test endpoint)
        if (businessDate != null) {
            statementRepository.findLatestPayableStatementByCardId(cardId).ifPresent(payable -> {
                if (payable.getDueDate() == null || payable.getStatementDate() == null) return;

                if (payable.getStatus() != null && payable.getStatus().startsWith("OVERDUE")) {
                    if (businessDate.isBefore(payable.getDueDate())) {
                        throw new IllegalArgumentException(
                                "For OVERDUE statement, businessDate cannot be earlier than dueDate (" + payable.getDueDate() + ")"
                        );
                    }
                }

                if (payable.getStatus() != null && payable.getStatus().startsWith("OPEN")) {
                    if (businessDate.isAfter(payable.getDueDate())) {
                        throw new IllegalArgumentException(
                                "For OPEN statement, businessDate cannot be later than dueDate (" + payable.getDueDate() + ")"
                        );
                    }
                }

                if ("BILLED".equals(payable.getStatus())) {
                    if (businessDate.isBefore(payable.getStatementDate())) {
                        throw new IllegalArgumentException(
                                "For BILLED statement, businessDate cannot be earlier than statementDate (" + payable.getStatementDate() + ")"
                        );
                    }
                }
            });
            //for OPEN
            statementRepository.findLatestOpenStatementByCardId(cardId).ifPresent(open -> {
                if (businessDate.isAfter(open.getDueDate())) {
                    throw new IllegalArgumentException(
                            "For OPEN statement, businessDate cannot be later than dueDate (" + open.getDueDate() + ")"
                    );
                }
                if (businessDate.isAfter(open.getStatementDate()) && businessDate.isBefore(open.getDueDate())) {
                    throw new IllegalArgumentException(
                            "For OPEN statement, businessDate cannot be between statementDate and dueDate (due: " + open.getDueDate() + "statement: " + open.getStatementDate() + ")"
                    );
                }
            });
        }

        BigDecimal pay = scaleMoney(amount);

        BigDecimal debt = safe(card.getCurrentDebt());
        BigDecimal newDebt = debt.subtract(pay);
        if (newDebt.compareTo(BigDecimal.ZERO) < 0) newDebt = BigDecimal.ZERO;

        card.setCurrentDebt(scaleMoney(newDebt));

        BigDecimal limit = safe(card.getLoanAmount());
        BigDecimal available = limit.subtract(safe(card.getCurrentDebt()));
        if (available.compareTo(BigDecimal.ZERO) < 0) available = BigDecimal.ZERO;

        card.setBalance(scaleMoney(available));
        creditCardRepository.save(card);

        // Insert transaction with requested business date (so jobs with ?date= work correctly)
        LocalDateTime txDateTime = (businessDate == null)
                ? LocalDateTime.now()
                : businessDate.atTime(12, 0);

        transactionHistoryRepository.insertTransactionAtDate(
                cardId, card.getUser().getUser_id(),
                cardId, card.getUser().getUser_id(),
                "DEBIT_TO_CREDIT_TEST",
                pay,
                BigDecimal.ZERO,
                txDateTime
        );

        // Sync repayment into OPEN statement
        registerRepayment(cardId, pay, businessDate);
    }

    // ===== Helpers =====

    private CreditCardStatement createOpenStatement(CreditCard card, LocalDate cycleStart) {
        normalizeCreditCardAccounting(card);

        LocalDate statementDate = cycleStart.plusDays(STATEMENT_PERIOD_DAYS);
        LocalDate dueDate = statementDate.plusDays(GRACE_PERIOD_DAYS);

        CreditCardStatement st = new CreditCardStatement();
        st.setStatementId(UUID.randomUUID().toString());
        st.setUserId(card.getUser().getUser_id());
        st.setCardId(card.getCardId());
        st.setStatementDate(statementDate);
        st.setDueDate(dueDate);

        // At creation time we store snapshot at cycle start (not final; finalizeOpenStatements will overwrite openingBalance)
        st.setOpeningBalance(scaleMoney(safe(card.getBalance())));

        st.setClosingBalance(null);
        st.setMinPaymentDue(BigDecimal.ZERO);
        st.setTotalPaymentDue(BigDecimal.ZERO);
        st.setInterestCharged(BigDecimal.ZERO);
        st.setFeesCharged(BigDecimal.ZERO);
        st.setPurchases(BigDecimal.ZERO);
        st.setPayments(BigDecimal.ZERO);

        st.setStatus("OPEN");
        return statementRepository.save(st);
    }

    private void createNextOpenStatementIfMissing(CreditCard card, LocalDate cycleStart) {
        if (statementRepository.findLatestOpenStatementByCardId(card.getCardId()).isPresent()) {
            return;
        }
        createOpenStatement(card, cycleStart);
    }

    private Totals calculateTotals(Long cardId, LocalDate start, LocalDate end) {
        LocalDateTime startDT = start.atStartOfDay();
        LocalDateTime endDTForJpqlExclusive = end.plusDays(1).atStartOfDay();
        LocalDateTime endDTForBetweenInclusive = end.atTime(LocalTime.MAX);

        BigDecimal purchases = scaleMoney(transactionHistoryRepository
                .sumAmountBySenderAndTypeLikeAndDateRange(cardId, TYPE_CREDIT_SPEND_LIKE, startDT, endDTForBetweenInclusive));

        BigDecimal creditFees = scaleMoney(transactionHistoryRepository
                .sumFeeBySenderAndTypeLikeAndDateRange(cardId, TYPE_CREDIT_SPEND_LIKE, startDT, endDTForBetweenInclusive));

        BigDecimal payments = scaleMoney(transactionHistoryRepository
                .sumIncomingAmountByCardAndTypes(cardId, PAYMENT_TYPES, startDT, endDTForJpqlExclusive));

        BigDecimal interest = scaleMoney(transactionHistoryRepository
                .sumAmountBySenderAndTypeLikeAndDateRange(cardId, TYPE_INTEREST, startDT, endDTForBetweenInclusive));

        BigDecimal lateFees = scaleMoney(transactionHistoryRepository
                .sumAmountBySenderAndTypeLikeAndDateRange(cardId, TYPE_LATE_FEE, startDT, endDTForBetweenInclusive));

        BigDecimal totalFees = scaleMoney(creditFees.add(lateFees));
        return new Totals(purchases, payments, interest, totalFees);
    }

    private BigDecimal sumPayments(Long cardId, LocalDate start, LocalDate end) {
        LocalDateTime startDT = start.atStartOfDay();
        LocalDateTime endExclusive = end.plusDays(1).atStartOfDay();

        return scaleMoney(transactionHistoryRepository
                .sumIncomingAmountByCardAndTypes(cardId, PAYMENT_TYPES, startDT, endExclusive));
    }

    private boolean alreadyPosted(Long cardId, String type, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);
        return transactionHistoryRepository.existsTransactionForCardAndTypeInDateRange(cardId, type, start, end);
    }

    private void postLateFee(Long cardId, Long userId, LocalDate date) {
        CreditCard card = creditCardRepository.findByCardId(cardId).orElse(null);
        if (card == null) return;

        normalizeCreditCardAccounting(card);

        if (alreadyPosted(cardId, TYPE_LATE_FEE, date)) {
            return; // idempotency
        }

        BigDecimal debt = safe(card.getCurrentDebt());
        if (debt.compareTo(BigDecimal.ZERO) <= 0) return;

        // late fee = debt * (LATE_FEE_AMOUNT / 100)
        BigDecimal lateFee = debt
                .multiply(LATE_FEE_AMOUNT)
                .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);

        if (lateFee.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal newDebt = scaleMoney(debt.add(lateFee));
        card.setCurrentDebt(newDebt);

        BigDecimal limit = safe(card.getLoanAmount());
        BigDecimal available = limit.subtract(newDebt);
        if (available.compareTo(BigDecimal.ZERO) < 0) available = BigDecimal.ZERO;

        card.setBalance(scaleMoney(available));
        creditCardRepository.save(card);

        transactionService.createTransaction(
                cardId, card.getUser().getUser_id(),
                cardId, card.getUser().getUser_id(),
                TYPE_LATE_FEE,
                lateFee,
                null,
                null,
                null,
                null
        );

        // Record late fee into latest OPEN statement (NOT into the old overdue/billed one)
        CreditCardStatement open = bootstrapIfMissing(cardId);

        open.setFeesCharged(scaleMoney(safe(open.getFeesCharged()).add(lateFee)));

        // IMPORTANT: keep totalPaymentDue growing when late fee accrues
        addToTotalPaymentDue(open, lateFee);

        statementRepository.save(open);
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal scaleMoney(BigDecimal v) {
        return safe(v).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal maxMoney(BigDecimal a, BigDecimal b) {
        BigDecimal aa = scaleMoney(a);
        BigDecimal bb = scaleMoney(b);
        return aa.max(bb);
    }

    /**
     * Increase totalPaymentDue by delta (delta >= 0), keep >= 0, and refresh minPaymentDue.
     */
    private void addToTotalPaymentDue(CreditCardStatement st, BigDecimal delta) {
        BigDecimal d = scaleMoney(delta);
        if (d.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal current = scaleMoney(st.getTotalPaymentDue());
        BigDecimal updated = current.add(d);

        st.setTotalPaymentDue(scaleMoney(updated));
        st.setMinPaymentDue(scaleMoney(updated.multiply(MIN_PAYMENT_RATE)));
    }

    /**
     * Decrease totalPaymentDue by amount (amount >= 0), keep >= 0, and refresh minPaymentDue.
     */
    private void subtractFromTotalPaymentDue(CreditCardStatement st, BigDecimal amount) {
        BigDecimal a = scaleMoney(amount);
        if (a.compareTo(BigDecimal.ZERO) <= 0) return;

        BigDecimal current = scaleMoney(st.getTotalPaymentDue());
        BigDecimal updated = current.subtract(a);
        if (updated.compareTo(BigDecimal.ZERO) < 0) updated = BigDecimal.ZERO;

        st.setTotalPaymentDue(scaleMoney(updated));
        st.setMinPaymentDue(scaleMoney(updated.multiply(MIN_PAYMENT_RATE)));
    }

    /**
     * Keeps credit card fields consistent for the MVP model:
     * loanAmount = limit, balance = available, currentDebt = debt
     */
    private void normalizeCreditCardAccounting(CreditCard card) {
        if (card == null) return;

        BigDecimal limit = safe(card.getLoanAmount());
        BigDecimal available = safe(card.getBalance());
        BigDecimal debt = card.getCurrentDebt(); // keep null detection

        // Derive debt when DB has null/0 but available is already reduced
        if (debt == null || debt.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal derivedDebt = limit.subtract(available);
            if (derivedDebt.compareTo(BigDecimal.ZERO) < 0) derivedDebt = BigDecimal.ZERO;
            card.setCurrentDebt(scaleMoney(derivedDebt));
        }

        // Keep invariant: available = limit - debt
        BigDecimal fixedAvailable = limit.subtract(safe(card.getCurrentDebt()));
        if (fixedAvailable.compareTo(BigDecimal.ZERO) < 0) fixedAvailable = BigDecimal.ZERO;

        if (fixedAvailable.compareTo(available) != 0) {
            card.setBalance(scaleMoney(fixedAvailable));
        }
    }

    private static class Totals {
        private final BigDecimal purchases;
        private final BigDecimal payments;
        private final BigDecimal interest;
        private final BigDecimal fees;

        private Totals(BigDecimal purchases, BigDecimal payments, BigDecimal interest, BigDecimal fees) {
            this.purchases = purchases;
            this.payments = payments;
            this.interest = interest;
            this.fees = fees;
        }
    }
}
