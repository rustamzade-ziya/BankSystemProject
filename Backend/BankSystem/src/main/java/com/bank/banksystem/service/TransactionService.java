package com.bank.banksystem.service;

import com.bank.banksystem.dto.response.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {

    List<TransactionResponse> getRecentTransactions(Long userId);

    List<TransactionResponse> getCardTransactions(Long cardId);

    TransactionResponse getTransactionDetails(Long transactionId);

    TransactionResponse createTransaction(Long senderId, Long senderUserId, Long receiverId, Long receiverUserId,
                                          String type, BigDecimal amount,
                                          BigDecimal fee);

}