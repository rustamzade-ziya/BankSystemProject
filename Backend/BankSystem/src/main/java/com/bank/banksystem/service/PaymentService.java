package com.bank.banksystem.service;

import com.bank.banksystem.dto.request.PaymentRequest;
import com.bank.banksystem.entity.CreditCard;
import com.bank.banksystem.entity.DebitCard;
import com.bank.banksystem.entity.TransactionHistory;
import com.bank.banksystem.repository.CreditCardRepository;
import com.bank.banksystem.repository.DebitCardRepository;
import com.bank.banksystem.repository.UserRepository;
import com.bank.banksystem.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private DebitCardRepository debitCardRepository;

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CashbackService cashbackService;

    @Autowired
    private EmailService emailService; // <<< ADDED FOR RECEIPT
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String withdraw(PaymentRequest request) {
        Long cardId = request.getSenderCardId();
        BigDecimal amount = request.getAmount();
        String serviceType = request.getServiceType();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return "Payment failed: Invalid amount";
        }

        // Check DebitCard first
        Optional<DebitCard> debitOpt = debitCardRepository.findById(cardId);
        if (debitOpt.isPresent()) {
            DebitCard debit = debitOpt.get();
            Long userId = debitCardRepository.getIdbyCardId(cardId);
            String userEmail = userRepository.findEmailbyId(userId);

            if (!debit.getUser().getUser_id().equals(userId)) {
                return "Payment failed: Card does not belong to user";
            }

            if (!"ACTIVE".equals(debit.getD_status())) {
                return "Payment failed: Card is not active";
            }

            String expiryDateStr = debit.getD_expiry_date();
            if (expiryDateStr == null || expiryDateStr.trim().isEmpty()) {
                return "Payment failed: Card expiry date is invalid";
            }

            try {
                LocalDate expiry = LocalDate.parse("01/" + expiryDateStr, DateTimeFormatter.ofPattern("dd/MM/yy"));
                expiry = expiry.withDayOfMonth(expiry.lengthOfMonth());
                if (expiry.isBefore(LocalDate.now())) {
                    return "Payment failed: Card has expired";
                }
            } catch (DateTimeParseException e) {
                return "Payment failed: Invalid card expiry date format";
            }

            BigDecimal balance = debit.getBalance();
            if (balance == null || balance.compareTo(amount) < 0) {
                return "Payment failed: Insufficient balance";
            }

            debit.setBalance(balance.subtract(amount));
            debitCardRepository.save(debit);

            transactionService.createTransaction(
                    cardId,
                    debit.getUser().getUser_id(),
                    null,
                    null,
                    "PAYMENT",
                    amount,
                    amount,
                    debit.getD_currency(),
                    debit.getD_currency(),
                    BigDecimal.ZERO);

            // Create transaction object for receipt
            TransactionHistory transaction = new TransactionHistory(
                    cardId, // senderId
                    null, // senderUserId (we don't have it here)
                    null, // receiverId
                    null, // receiverUserId
                    "PAYMENT", // type
                    amount, // amount
                    amount, // convertedAmount (same as amount for payment)
                    BigDecimal.ZERO, // fee
                    null, // senderCurrency (unknown here)
                    null // receiverCurrency (unknown here)
            );

            // Send receipt email
            try {
                emailService.sendReceiptEmail(userEmail, cardId, null, "PAYMENT", amount, amount, debit.getD_currency(),
                        debit.getD_currency(), BigDecimal.ZERO);
            } catch (Exception e) {
                System.err.println("Failed to send receipt email: " + e.getMessage());
            }

            BigDecimal cashbackAmount = BigDecimal.ZERO;
            try {
                if (serviceType != null && !serviceType.trim().isEmpty()) {
                    cashbackAmount = cashbackService.addCashback(userId, cardId, amount, serviceType);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (cashbackAmount.compareTo(BigDecimal.ZERO) > 0) {
                return "Payment successful. Your cashback: " + cashbackAmount;
            } else {
                return "Payment successful";
            }
        }

        // Check CreditCard
        Optional<CreditCard> creditOpt = creditCardRepository.findById(cardId);
        if (creditOpt.isPresent()) {
            CreditCard credit = creditOpt.get();
            Long userId = creditCardRepository.getIdbyCardId(cardId);
            String userEmail = userRepository.findEmailbyId(userId);

            if (!"ACTIVE".equals(credit.getStatus())) {
                return "Payment failed: Card is not active";
            }

            String expiryDateStr = credit.getExpiryDate();
            if (expiryDateStr == null || expiryDateStr.trim().isEmpty()) {
                return "Payment failed: Card expiry date is invalid";
            }

            try {
                LocalDate expiry = LocalDate.parse("01/" + expiryDateStr, DateTimeFormatter.ofPattern("dd/MM/yy"));
                expiry = expiry.withDayOfMonth(expiry.lengthOfMonth());
                if (expiry.isBefore(LocalDate.now())) {
                    return "Payment failed: Card has expired";
                }
            } catch (DateTimeParseException e) {
                return "Payment failed: Invalid card expiry date format";
            }

            BigDecimal balance = credit.getBalance();
            if (balance == null || balance.compareTo(amount) < 0) {
                return "Payment failed: Insufficient balance";
            }

            credit.setBalance(balance.subtract(amount));
            creditCardRepository.save(credit);

            transactionService.createTransaction(
                    cardId,
                    credit.getUser().getUser_id(),
                    null,
                    null,
                    "PAYMENT",
                    amount,
                    amount,
                    credit.getCurrency(),
                    credit.getCurrency(),
                    BigDecimal.ZERO);

            // Create transaction object for receipt
            TransactionHistory transaction = new TransactionHistory(
                    cardId, // senderId
                    null, // senderUserId (we don't have it here)
                    null, // receiverId
                    null, // receiverUserId
                    "PAYMENT", // type
                    amount, // amount
                    amount, // convertedAmount (same as amount for payment)
                    BigDecimal.ZERO, // fee
                    null, // senderCurrency (unknown here)
                    null // receiverCurrency (unknown here)
            );

            // Send receipt email
            try {
                emailService.sendReceiptEmail(userEmail, cardId, null, "PAYMENT", amount, amount, credit.getCurrency(),
                        credit.getCurrency(), BigDecimal.ZERO);
            } catch (Exception e) {
                System.err.println("Failed to send receipt email: " + e.getMessage());
            }

            return "Payment successful";
        }

        return "Payment failed: Card not found";
    }

}
