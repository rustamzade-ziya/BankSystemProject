package com.bank.banksystem.service.Impl;

import com.bank.banksystem.dto.request.TransferRequest;
import com.bank.banksystem.dto.response.TransferResponse;
import com.bank.banksystem.entity.CreditCard;
import com.bank.banksystem.entity.DebitCard;
import com.bank.banksystem.repository.CreditCardRepository;
import com.bank.banksystem.repository.DebitCardRepository;
import com.bank.banksystem.repository.TransactionHistoryRepository;
import com.bank.banksystem.repository.UserRepository;
import com.bank.banksystem.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
public class TransferServiceImpl implements TransferService {

    private final CreditCardRepository creditCardRepository;
    private final DebitCardRepository debitCardRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final TransactionService transactionService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final CurrencyConversionService currencyConversionService;
    private final CreditCardStatementService creditCardStatementService;


    private static final BigDecimal CREDIT_CARD_FEE_PERCENT = new BigDecimal("0.01"); // 1%
    private static final BigDecimal EXTERNAL_TRANSFER_FEE_PERCENT = new BigDecimal("0.005"); // 0.5%

    public TransferServiceImpl(
            DebitCardRepository debitCardRepository,
            CreditCardRepository creditCardRepository,
            TransactionHistoryRepository transactionHistoryRepository,
            TransactionService transactionService,
            EmailService emailService,
            UserRepository userRepository,
            CurrencyConversionService currencyConversionService,
            CreditCardStatementService creditCardStatementService

    ) {
        this.creditCardRepository = creditCardRepository;
        this.debitCardRepository = debitCardRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.transactionService = transactionService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.currencyConversionService = currencyConversionService;
        this.creditCardStatementService = creditCardStatementService;
    }

    @Override
    @Transactional
    public TransferResponse performTransfer(TransferRequest request) {

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return createErrorResponse("Invalid amount. Amount must be greater than zero.");
        }

        Optional<DebitCard> senderDebitOpt = debitCardRepository.findByCardId(request.getSenderAccountNumber());
        Optional<CreditCard> senderCreditOpt = creditCardRepository.findByCardId(request.getSenderAccountNumber());

        if (senderDebitOpt.isEmpty() && senderCreditOpt.isEmpty()) {
            return createErrorResponse("Sender card not found");
        }

        if (request.getSenderAccountNumber().equals(request.getReceiverAccountNumber())) {
            return createErrorResponse("Sender and receiver cannot be the same");
        }

        boolean isSenderDebit = senderDebitOpt.isPresent();

        Optional<DebitCard> receiverDebitOpt = debitCardRepository.findByCardId(request.getReceiverAccountNumber());
        Optional<CreditCard> receiverCreditOpt = creditCardRepository.findByCardId(request.getReceiverAccountNumber());

        boolean isReceiverInOurSystem = receiverDebitOpt.isPresent() || receiverCreditOpt.isPresent();
        boolean isReceiverDebit = receiverDebitOpt.isPresent();

        try {
            // Validate sender
            if (isSenderDebit) {
                DebitCard sender = senderDebitOpt.get();
                if (!isDebitCardValid(sender.getD_expiry_date())) {
                    return createErrorResponse("Sender card expired or invalid");
                }
                if (!"ACTIVE".equals(sender.getD_status())) {
                    return createErrorResponse("Sender card is not active");
                }
            } else {
                CreditCard sender = senderCreditOpt.get();
                if (!isCreditCardValid(sender.getExpiryDate())) {
                    return createErrorResponse("Sender credit card expired");
                }
                if (!"ACTIVE".equals(sender.getStatus())) {
                    return createErrorResponse("Sender credit card is not active");
                }
            }

            // Validate receiver if internal
            if (isReceiverInOurSystem) {
                if (isReceiverDebit) {
                    DebitCard receiver = receiverDebitOpt.get();
                    if (!isDebitCardValid(receiver.getD_expiry_date()) || !"ACTIVE".equals(receiver.getD_status())) {
                        return createErrorResponse("Receiver card invalid or inactive");
                    }
                } else {
                    CreditCard receiver = receiverCreditOpt.get();
                    if (!isCreditCardValid(receiver.getExpiryDate()) || !"ACTIVE".equals(receiver.getStatus())) {
                        return createErrorResponse("Receiver card invalid or inactive");
                    }
                }
            }

            // Process transfer
            TransferResponse response;
            if (isSenderDebit) {
                DebitCard sender = senderDebitOpt.get();
                Long userId = debitCardRepository.getIdbyCardId(request.getSenderAccountNumber());
                String userEmail = userRepository.findEmailbyId(userId);
                if (isReceiverInOurSystem) {
                    response = isReceiverDebit
                            ? handleInternalDebitToDebitTransfer(sender, receiverDebitOpt.get(), request.getAmount(), userEmail)
                            : handleInternalDebitToCreditTransfer(sender, receiverCreditOpt.get(), request.getAmount(), userEmail);
                } else {
                    response = handleExternalDebitTransfer(sender, request.getReceiverAccountNumber(), request.getAmount(), userEmail);
                }
            } else {
                CreditCard sender = senderCreditOpt.get();
                Long userId = creditCardRepository.getIdbyCardId(request.getSenderAccountNumber());
                String userEmail = userRepository.findEmailbyId(userId);
                if (isReceiverInOurSystem) {
                    response = isReceiverDebit
                            ? handleCreditToDebitTransferWithFee(sender, receiverDebitOpt.get(), request.getAmount(), userEmail)
                            : handleCreditToCreditTransferWithFee(sender, receiverCreditOpt.get(), request.getAmount(), userEmail);
                } else {
                    response = handleExternalCreditTransfer(sender, request.getReceiverAccountNumber(), request.getAmount(), userEmail);
                }
            }

            return response;

        } catch (Exception e) {
            return createErrorResponse("Transfer error: " + e.getMessage());
        }
    }

    // ------------------- Internal Transfers with Currency Conversion -------------------

    private TransferResponse handleInternalDebitToDebitTransfer(DebitCard sender, DebitCard receiver,
                                                                BigDecimal amount, String userEmail) {

        // Convert if currencies differ
        BigDecimal convertedAmount = currencyConversionService.convert(
                amount,
                sender.getD_currency(),
                receiver.getD_currency()
        );

        if (sender.getBalance().compareTo(amount) < 0) {
            return createErrorResponse("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(convertedAmount));

        debitCardRepository.save(sender);
        debitCardRepository.save(receiver);

        transactionService.createTransaction(
                sender.getCardId(),
                sender.getUser().getUser_id(),
                receiver.getCardId(),
                receiver.getUser().getUser_id(),
                "DEBIT_TO_DEBIT_INTERNAL",
                amount,
                convertedAmount,
                sender.getD_currency(),
                receiver.getD_currency(),
                BigDecimal.ZERO
        );

        emailService.sendReceiptEmail(
                userEmail,
                sender.getCardId(),
                receiver.getCardId(),
                "DEBIT_TO_DEBIT_INTERNAL",
                amount,
                convertedAmount,
                sender.getD_currency(),
                receiver.getD_currency(),
                BigDecimal.ZERO
        );

        return createSuccessResponse("Transfer from debit to debit completed. Fee: 0%");
    }

    private TransferResponse handleInternalDebitToCreditTransfer(DebitCard sender, CreditCard receiver,
                                                                 BigDecimal amount, String userEmail) {

        // Convert if currencies differ
        BigDecimal convertedAmount = currencyConversionService.convert(
                amount,
                sender.getD_currency(),
                receiver.getCurrency()
        );

        if (sender.getBalance().compareTo(amount) < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return createErrorResponse("Insufficient funds");
        }

        normalizeCreditCardAccounting(receiver);
        BigDecimal debt = safe(receiver.getCurrentDebt());

        if (debt.compareTo(BigDecimal.ZERO) <= 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return createErrorResponse("Credit card has no debt to repay.");
        }

        if (amount.compareTo(debt) > 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return createErrorResponse("Repayment amount exceeds current debt");
        }

        sender.setBalance(scaleMoney(sender.getBalance().subtract(amount)));

        BigDecimal limit = safe(receiver.getLoanAmount());
        BigDecimal available = safe(receiver.getBalance());

        receiver.setCurrentDebt(scaleMoney(debt.subtract(amount)));

        BigDecimal newAvailable = available.add(amount);
        if (newAvailable.compareTo(limit) > 0) {
            newAvailable = limit;
        }
        receiver.setBalance(scaleMoney(newAvailable));

        debitCardRepository.save(sender);
        creditCardRepository.save(receiver);

        transactionService.createTransaction(
                sender.getCardId(),
                sender.getUser().getUser_id(),
                receiver.getCardId(),
                receiver.getUser().getUser_id(),
                "DEBIT_TO_CREDIT_INTERNAL",
                amount,
                convertedAmount,
                sender.getD_currency(),
                receiver.getCurrency(),
                BigDecimal.ZERO
        );


        emailService.sendReceiptEmail(
                userEmail,
                sender.getCardId(),
                receiver.getCardId(),
                "DEBIT_TO_CREDIT_INTERNAL",
                amount,
                convertedAmount,
                sender.getD_currency(),
                receiver.getCurrency(),
                BigDecimal.ZERO
        );

        return createSuccessResponse("Transfer from debit to credit completed. Fee: 0%");
    }

    private TransferResponse handleCreditToDebitTransferWithFee(CreditCard sender, DebitCard receiver,
                                                                BigDecimal amount, String userEmail) {

        BigDecimal convertedAmount = currencyConversionService.convert(
                amount,
                sender.getCurrency(),
                receiver.getD_currency()
        );
        normalizeCreditCardAccounting(sender);

        BigDecimal fee = amount.multiply(CREDIT_CARD_FEE_PERCENT);
        BigDecimal totalAmount = amount.add(fee);


        BigDecimal available = safe(sender.getBalance());
        BigDecimal debt = safe(sender.getCurrentDebt());

        if (available.compareTo(totalAmount) < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return createErrorResponse("Insufficient available credit (including 1% fee)");
        }

        if (sender.getBalance().compareTo(totalAmount) < 0) {
            return createErrorResponse("Insufficient funds including fee");
        }

        creditCardStatementService.ensureActiveStatementExists(sender);

        sender.setBalance(sender.getBalance().subtract(totalAmount));
        sender.setCurrentDebt(scaleMoney(debt.add(totalAmount)));

        receiver.setBalance(receiver.getBalance().add(convertedAmount));

        creditCardRepository.save(sender);
        debitCardRepository.save(receiver);

        transactionService.createTransaction(
                sender.getCardId(),
                sender.getUser().getUser_id(),
                receiver.getCardId(),
                receiver.getUser().getUser_id(),
                "CREDIT_TO_DEBIT_INTERNAL",
                amount,
                convertedAmount,
                sender.getCurrency(),
                receiver.getD_currency(),
                fee
        );


        emailService.sendReceiptEmail(
                userEmail,
                sender.getCardId(),
                receiver.getCardId(),
                "CREDIT_TO_DEBIT_INTERNAL",
                amount,
                convertedAmount,
                sender.getCurrency(),
                receiver.getD_currency(),
                fee
        );

        return createSuccessResponse(String.format("Transfer from credit to debit completed. Fee: %s", fee));
    }

    private TransferResponse handleCreditToCreditTransferWithFee(CreditCard sender, CreditCard receiver,
                                                                 BigDecimal amount, String userEmail) {

        BigDecimal convertedAmount = currencyConversionService.convert(
                amount,
                sender.getCurrency(),
                receiver.getCurrency()
        );

        normalizeCreditCardAccounting(sender);
        normalizeCreditCardAccounting(receiver);

        BigDecimal fee = amount.multiply(CREDIT_CARD_FEE_PERCENT);
        BigDecimal totalAmount = amount.add(fee);

        BigDecimal senderAvailable = safe(sender.getBalance());
        if (senderAvailable.compareTo(totalAmount) < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return createErrorResponse("Insufficient available credit on sender (including 1% fee).");
        }

        BigDecimal receiverDebt = safe(receiver.getCurrentDebt());
        if (receiverDebt.compareTo(BigDecimal.ZERO) <= 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return createErrorResponse("Receiver credit card has no debt to repay.");
        }

        if (amount.compareTo(receiverDebt) > 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return createErrorResponse("Repayment amount exceeds receiver current debt.");
        }
        creditCardStatementService.ensureActiveStatementExists(sender);

        BigDecimal senderDebt = safe(sender.getCurrentDebt());
        sender.setBalance(scaleMoney(senderAvailable.subtract(totalAmount)));
        sender.setCurrentDebt(scaleMoney(senderDebt.add(totalAmount)));

        BigDecimal receiverLimit = safe(receiver.getLoanAmount());
        BigDecimal receiverAvailable = safe(receiver.getBalance());

        receiver.setCurrentDebt(scaleMoney(receiverDebt.subtract(amount)));

        BigDecimal newAvailable = receiverAvailable.add(amount);
        if (newAvailable.compareTo(receiverLimit) > 0) {
            newAvailable = receiverLimit;
        }
        receiver.setBalance(scaleMoney(newAvailable));

        creditCardRepository.save(sender);
        creditCardRepository.save(receiver);

        transactionService.createTransaction(
                sender.getCardId(),
                sender.getUser().getUser_id(),
                receiver.getCardId(),
                receiver.getUser().getUser_id(),
                "CREDIT_TO_CREDIT_INTERNAL",
                amount,
                convertedAmount,
                sender.getCurrency(),
                receiver.getCurrency(),
                fee
        );


        emailService.sendReceiptEmail(
                userEmail,
                sender.getCardId(),
                receiver.getCardId(),
                "CREDIT_TO_CREDIT_INTERNAL",
                amount,
                convertedAmount,
                sender.getCurrency(),
                receiver.getCurrency(),
                fee
        );

        return createSuccessResponse(String.format("Transfer from credit to credit completed. Fee: %s", fee));
    }

    // ------------------- External Transfers (unchanged) -------------------

    private TransferResponse handleExternalDebitTransfer(DebitCard sender, Long receiverAccountNumber,
                                                         BigDecimal amount, String userEmail) {
        BigDecimal fee = amount.multiply(EXTERNAL_TRANSFER_FEE_PERCENT);
        BigDecimal totalAmount = amount.add(fee);

        if (sender.getBalance().compareTo(totalAmount) < 0) {
            return createErrorResponse("Insufficient funds including fee");
        }

        sender.setBalance(sender.getBalance().subtract(totalAmount));
        debitCardRepository.save(sender);

        transactionService.createTransaction(
                sender.getCardId(),
                sender.getUser().getUser_id(),
                null,
                null,
                "DEBIT_TO_EXTERNAL",
                amount,
                amount,
                sender.getD_currency(),
                sender.getD_currency(),
                fee
        );

        emailService.sendReceiptEmail(
                userEmail,
                sender.getCardId(),
                null,
                "DEBIT_TO_EXTERNAL",
                amount,
                amount,
                sender.getD_currency(),
                sender.getD_currency(),
                fee
        );

        return createSuccessResponse(String.format("Transfer to external completed. Fee: %s", fee));
    }

    private TransferResponse handleExternalCreditTransfer(CreditCard sender, Long receiverAccountNumber,
                                                          BigDecimal amount, String userEmail) {
        normalizeCreditCardAccounting(sender);

        BigDecimal fee = amount.multiply(CREDIT_CARD_FEE_PERCENT);
        BigDecimal totalAmount = amount.add(fee);

        BigDecimal available = safe(sender.getBalance());
        if (available.compareTo(totalAmount) < 0) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return createErrorResponse("Insufficient available credit (including 1% fee)");
        }

        BigDecimal debt = safe(sender.getCurrentDebt());
        sender.setBalance(sender.getBalance().subtract(totalAmount));
        sender.setCurrentDebt(scaleMoney(debt.add(totalAmount)));


        creditCardRepository.save(sender);

        transactionService.createTransaction(
                sender.getCardId(),
                sender.getUser().getUser_id(),
                null,
                null,
                "CREDIT_TO_EXTERNAL",
                amount,
                amount,
                sender.getCurrency(),
                sender.getCurrency(),
                fee
        );

        emailService.sendReceiptEmail(
                userEmail,
                sender.getCardId(),
                null,
                "CREDIT_TO_EXTERNAL",
                amount,
                amount,
                sender.getCurrency(),
                sender.getCurrency(),
                fee
        );

        return createSuccessResponse(String.format("Transfer to external completed. Fee: %s", fee));
    }

    // ------------------- Validation -------------------

    private boolean isDebitCardValid(String expiryDate) {
        if (expiryDate == null || expiryDate.trim().isEmpty()) return false;
        try {
            LocalDate expiry = LocalDate.parse("01/" + expiryDate, DateTimeFormatter.ofPattern("dd/MM/yy"));
            expiry = expiry.withDayOfMonth(expiry.lengthOfMonth());
            return !expiry.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isCreditCardValid(String expiryDate) {
        if (expiryDate == null || expiryDate.trim().isEmpty()) return false;
        try {
            LocalDate expiry = LocalDate.parse("01/" + expiryDate, DateTimeFormatter.ofPattern("dd/MM/yy"));
            expiry = expiry.withDayOfMonth(expiry.lengthOfMonth());
            return !expiry.isBefore(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private TransferResponse createSuccessResponse(String message) {
        TransferResponse response = new TransferResponse();
        response.setSuccess(true);
        response.setMessage(message);
        return response;
    }

    private TransferResponse createErrorResponse(String message) {
        TransferResponse response = new TransferResponse();
        response.setSuccess(false);
        response.setMessage(message);
        return response;
    }

    private BigDecimal safe(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private BigDecimal scaleMoney(BigDecimal v) {
        return safe(v).setScale(2, RoundingMode.HALF_UP);
    }

    // Keeps credit card fields consistent for the MVP model:
    // loanAmount = limit, balance = available, currentDebt = debt
    private void normalizeCreditCardAccounting(CreditCard card) {
        if (card == null) {
            return;
        }

        BigDecimal limit = safe(card.getLoanAmount());
        BigDecimal available = safe(card.getBalance());
        BigDecimal debt = card.getCurrentDebt(); // keep null detection

        // Derive debt when DB has null/0 but available is already reduced
        if (debt == null || debt.compareTo(BigDecimal.ZERO) <= 0) {
            BigDecimal derivedDebt = limit.subtract(available);
            if (derivedDebt.compareTo(BigDecimal.ZERO) < 0) {
                derivedDebt = BigDecimal.ZERO;
            }
            card.setCurrentDebt(scaleMoney(derivedDebt));
        }

        // Keep invariant: available = limit - debt
        BigDecimal fixedAvailable = limit.subtract(safe(card.getCurrentDebt()));
        if (fixedAvailable.compareTo(BigDecimal.ZERO) < 0) {
            fixedAvailable = BigDecimal.ZERO;
        }

        // Только если текущий available не совпадает с рассчитанным
        if (fixedAvailable.compareTo(available) != 0) {
            card.setBalance(scaleMoney(fixedAvailable));
        }

    }
}
