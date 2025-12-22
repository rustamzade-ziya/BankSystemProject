package com.bank.banksystem.service.Impl;

import com.bank.banksystem.dto.response.TransactionResponse;
import com.bank.banksystem.entity.TransactionHistory;

import com.bank.banksystem.repository.TransactionHistoryRepository;
import com.bank.banksystem.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionServiceimpl implements TransactionService {

    @Autowired
    private TransactionHistoryRepository transactionHistoryRepository;

    @Override
    public List<TransactionResponse> getRecentTransactions(Long userId) {
        List<TransactionHistory> history = transactionHistoryRepository.getRecentTransactions(userId, 10);

        return history.stream()
                .map(this::transfromDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionResponse> getCardTransactions(Long cardId) {
        List<TransactionHistory> history = transactionHistoryRepository.getCardTransactions(cardId);

        return history.stream()
                .map(this::transfromDto)
                .collect(Collectors.toList());
    }

    @Override
    public TransactionResponse getTransactionDetails(Long transactionId) {
        TransactionHistory transaction = transactionHistoryRepository.getTransactionDetails(transactionId);

        if (transaction == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND, "Transaction not found with ID: " + transactionId);
        }

        return transfromDto(transaction);
    }

    @Override
    public TransactionResponse createTransaction(
            Long senderId,
            Long senderUserId,
            Long receiverId,
            Long receiverUserId,
            String type,
            BigDecimal amount,
            BigDecimal convertedAmount,
            String senderCurrency,
            String receiverCurrency,
            BigDecimal fee
    ) {

        TransactionHistory nt = new TransactionHistory(
                senderId,
                senderUserId,
                receiverId,
                receiverUserId,
                type,
                amount,
                convertedAmount,
                fee,
                senderCurrency,
                receiverCurrency
        );

        nt.setFee(fee);

        TransactionHistory saved = transactionHistoryRepository.save(nt);
        return transfromDto(saved);
    }

    private TransactionResponse transfromDto(TransactionHistory tr) {
        return new TransactionResponse(
                tr.getId(),
                tr.getSenderId(),
                tr.getReceiverId(),
                tr.getType(),
                tr.getAmount(),
                tr.getConvertedAmount(),
                tr.getSenderCurrency(),
                tr.getReceiverCurrency(),
                tr.getDate());
    }
}