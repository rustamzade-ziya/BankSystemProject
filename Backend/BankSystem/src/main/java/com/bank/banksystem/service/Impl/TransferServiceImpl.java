package com.bank.banksystem.service.Impl;

import com.bank.banksystem.dto.request.TransferRequest;
import com.bank.banksystem.dto.response.TransferResponse;
import com.bank.banksystem.entity.CreditCard;
import com.bank.banksystem.entity.DebitCard;
import com.bank.banksystem.repository.CreditCardRepository;
import com.bank.banksystem.repository.DebitCardRepository;
import com.bank.banksystem.repository.TransactionHistoryRepository;
import com.bank.banksystem.repository.UserRepository;
import com.bank.banksystem.service.CurrencyConversionService;
import com.bank.banksystem.service.EmailService;
import com.bank.banksystem.service.TransactionService;
import com.bank.banksystem.service.TransferService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    private static final BigDecimal CREDIT_CARD_FEE_PERCENT = new BigDecimal("0.01"); // 1%
    private static final BigDecimal EXTERNAL_TRANSFER_FEE_PERCENT = new BigDecimal("0.005"); // 0.5%

    public TransferServiceImpl(
            DebitCardRepository debitCardRepository,
            CreditCardRepository creditCardRepository,
            TransactionHistoryRepository transactionHistoryRepository,
            TransactionService transactionService,
            EmailService emailService,
            UserRepository userRepository,
            CurrencyConversionService currencyConversionService
    ) {
        this.creditCardRepository = creditCardRepository;
        this.debitCardRepository = debitCardRepository;
        this.transactionHistoryRepository = transactionHistoryRepository;
        this.transactionService = transactionService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.currencyConversionService = currencyConversionService;
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

        transactionService.createTransaction(sender.getCardId(), sender.getUser().getUser_id(),
                receiver.getCardId(), receiver.getUser().getUser_id(),
                "DEBIT_TO_DEBIT_INTERNAL", amount, BigDecimal.ZERO);

        emailService.sendReceiptEmail(userEmail, sender.getCardId(), receiver.getCardId(),
                "DEBIT_TO_DEBIT_INTERNAL", amount.toString(), "0");

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
            return createErrorResponse("Insufficient funds");
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(convertedAmount));

        debitCardRepository.save(sender);
        creditCardRepository.save(receiver);

        transactionService.createTransaction(sender.getCardId(), sender.getUser().getUser_id(),
                receiver.getCardId(), receiver.getUser().getUser_id(),
                "DEBIT_TO_CREDIT_INTERNAL", amount, BigDecimal.ZERO);

        emailService.sendReceiptEmail(userEmail, sender.getCardId(), receiver.getCardId(),
                "DEBIT_TO_CREDIT_INTERNAL", amount.toString(), "0");

        return createSuccessResponse("Transfer from debit to credit completed. Fee: 0%");
    }

    private TransferResponse handleExternalDebitTransfer(DebitCard sender, Long receiverAccountNumber,
                                                         BigDecimal amount, String userEmail) {
        BigDecimal fee = amount.multiply(EXTERNAL_TRANSFER_FEE_PERCENT);
        BigDecimal totalAmount = amount.add(fee);

        if (sender.getBalance().compareTo(totalAmount) < 0) {
            return createErrorResponse("Insufficient funds including fee");
        }

        sender.setBalance(sender.getBalance().subtract(totalAmount));
        debitCardRepository.save(sender);

        transactionService.createTransaction(sender.getCardId(), sender.getUser().getUser_id(),
                receiverAccountNumber, null,
                "DEBIT_TO_EXTERNAL", amount, fee);

        emailService.sendReceiptEmail(userEmail, sender.getCardId(), receiverAccountNumber,
                "DEBIT_TO_EXTERNAL", amount.toString(), fee.toString());

        return createSuccessResponse(String.format("Transfer to external completed. Fee: %s", fee));
    }

    private TransferResponse handleCreditToDebitTransferWithFee(CreditCard sender, DebitCard receiver,
                                                                BigDecimal amount, String userEmail) {

        BigDecimal convertedAmount = currencyConversionService.convert(
                amount,
                sender.getCurrency(),
                receiver.getD_currency()
        );

        BigDecimal fee = amount.multiply(CREDIT_CARD_FEE_PERCENT);
        BigDecimal totalAmount = amount.add(fee);

        if (sender.getBalance().compareTo(totalAmount) < 0) {
            return createErrorResponse("Insufficient funds including fee");
        }

        sender.setBalance(sender.getBalance().subtract(totalAmount));
        receiver.setBalance(receiver.getBalance().add(amount));

        creditCardRepository.save(sender);
        debitCardRepository.save(receiver);

        transactionService.createTransaction(sender.getCardId(), sender.getUser().getUser_id(),
                receiver.getCardId(), receiver.getUser().getUser_id(),
                "CREDIT_TO_DEBIT_WITH_FEE", amount, fee);

        emailService.sendReceiptEmail(userEmail, sender.getCardId(), receiver.getCardId(),
                "CREDIT_TO_DEBIT_WITH_FEE", amount.toString(), fee.toString());

        return createSuccessResponse(String.format("Transfer from credit to debit completed. Fee: %s", fee));
    }

    private TransferResponse handleCreditToCreditTransferWithFee(CreditCard sender, CreditCard receiver,
                                                                 BigDecimal amount, String userEmail) {
        if (!sender.getCurrency().equals(receiver.getCurrency())) {
            return createErrorResponse("Currency mismatch");
        }

        BigDecimal fee = amount.multiply(CREDIT_CARD_FEE_PERCENT);
        BigDecimal totalAmount = amount.add(fee);

        if (sender.getBalance().compareTo(totalAmount) < 0) {
            return createErrorResponse("Insufficient funds including fee");
        }

        sender.setBalance(sender.getBalance().subtract(totalAmount));
        receiver.setBalance(receiver.getBalance().add(amount));

        creditCardRepository.save(sender);
        creditCardRepository.save(receiver);

        transactionService.createTransaction(sender.getCardId(), sender.getUser().getUser_id(),
                receiver.getCardId(), receiver.getUser().getUser_id(),
                "CREDIT_TO_CREDIT_WITH_FEE", amount, fee);

        emailService.sendReceiptEmail(userEmail, sender.getCardId(), receiver.getCardId(),
                "CREDIT_TO_CREDIT_WITH_FEE", amount.toString(), fee.toString());

        return createSuccessResponse(String.format("Transfer from credit to credit completed. Fee: %s", fee));
    }

    private TransferResponse handleExternalCreditTransfer(CreditCard sender, Long receiverAccountNumber,
                                                          BigDecimal amount, String userEmail) {
        BigDecimal fee = amount.multiply(CREDIT_CARD_FEE_PERCENT);
        BigDecimal totalAmount = amount.add(fee);

        if (sender.getBalance().compareTo(totalAmount) < 0) {
            return createErrorResponse("Insufficient funds including fee");
        }

        sender.setBalance(sender.getBalance().subtract(totalAmount));
        creditCardRepository.save(sender);

        transactionService.createTransaction(sender.getCardId(), sender.getUser().getUser_id(),
                receiverAccountNumber, null,
                "CREDIT_TO_EXTERNAL_WITH_FEE", amount, fee);

        emailService.sendReceiptEmail(userEmail, sender.getCardId(), receiverAccountNumber,
                "CREDIT_TO_EXTERNAL_WITH_FEE", amount.toString(), fee.toString());

        return createSuccessResponse(String.format("Transfer to external completed. Fee: %s", fee));
    }


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
}
