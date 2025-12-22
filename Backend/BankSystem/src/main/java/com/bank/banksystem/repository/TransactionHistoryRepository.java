package com.bank.banksystem.repository;

import com.bank.banksystem.entity.TransactionHistory;
import jakarta.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    // Get recent transactions for user (last N transactions)
    @Query(value = """
                        SELECT * FROM transaction_history
                        WHERE tr_sender_user_id = :userId OR tr_receiver_user_id = :userId
                        ORDER BY tr_date DESC
                        LIMIT :limit
                        """, nativeQuery = true)
    List<TransactionHistory> getRecentTransactions(
            @Param("userId") Long userId,
            @Param("limit") int limit);

    // Get transactions for specific card
    @Query(value = """
                        SELECT * FROM transaction_history
                        WHERE tr_sender_id = :cardId OR tr_receiver_id = :cardId
                        ORDER BY tr_date DESC
                        """, nativeQuery = true)
    List<TransactionHistory> getCardTransactions(@Param("cardId") Long cardId);

    // Get transaction details by ID
    @Query(value = "SELECT * FROM transaction_history WHERE tr_id = :transactionId", nativeQuery = true)
    TransactionHistory getTransactionDetails(@Param("transactionId") Long transactionId);

    // Get transactions between dates
    @Query(value = """
                        SELECT * FROM transaction_history
                        WHERE (tr_sender_user_id = :userId OR tr_receiver_user_id = :userId)
                        AND tr_date BETWEEN :startDate AND :endDate
                        ORDER BY tr_date DESC
                        """, nativeQuery = true)
    List<TransactionHistory> getTransactionsByDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Get transactions by type
    @Query(value = """
                        SELECT * FROM transaction_history
                        WHERE (tr_sender_user_id = :userId OR tr_receiver_user_id = :userId)
                        AND tr_type = :type
                        ORDER BY tr_date DESC
                        """, nativeQuery = true)
    List<TransactionHistory> getTransactionsByType(
            @Param("userId") Long userId,
            @Param("type") String type);
}