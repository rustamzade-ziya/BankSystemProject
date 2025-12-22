package com.bank.banksystem.repository;

import com.bank.banksystem.entity.TransactionHistory;
import jakarta.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    // =========================
    // Aggregations for statements
    // =========================

    @Query("""
        SELECT COALESCE(SUM(t.amount), 0)
        FROM TransactionHistory t
        WHERE t.senderId = :cardId
          AND t.type IN :types
          AND t.date >= :startDate
          AND t.date < :endDate
    """)
    BigDecimal sumOutgoingAmountByCardAndTypes(
            @Param("cardId") Long cardId,
            @Param("types") List<String> types,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("""
        SELECT COALESCE(SUM(t.fee), 0)
        FROM TransactionHistory t
        WHERE t.senderId = :cardId
          AND t.type IN :types
          AND t.date >= :startDate
          AND t.date < :endDate
    """)
    BigDecimal sumOutgoingFeesByCardAndTypes(
            @Param("cardId") Long cardId,
            @Param("types") List<String> types,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );


    @Query(value = "SELECT * FROM transaction_history WHERE tr_sender_id = :cardId ORDER BY tr_date DESC LIMIT 1", nativeQuery = true)
    TransactionHistory findLastTransactionByCardId(@Param("cardId") Long cardId);

    @Query(value = "SELECT * FROM transaction_history WHERE tr_sender_id = :cardId OR tr_receiver_id = :cardId ORDER BY tr_date DESC", nativeQuery = true)
    List<TransactionHistory> findAllTransactionsByCardId(@Param("cardId") Long cardId);

    @Query(value = "SELECT * FROM transaction_history WHERE tr_sender_id = :cardId OR tr_receiver_id = :cardId ORDER BY tr_date DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<TransactionHistory> findTransactionsByCardIdPaged(@Param("cardId") Long cardId, @Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "SELECT COUNT(*) FROM transaction_history WHERE tr_sender_id = :cardId OR tr_receiver_id = :cardId", nativeQuery = true)
    long countTransactionsByCardId(@Param("cardId") Long cardId);

    @Query(value = "SELECT COALESCE(SUM(tr_amount), 0) FROM transaction_history WHERE tr_sender_id = :cardId AND tr_type LIKE :typePattern AND tr_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    BigDecimal sumAmountBySenderAndTypeLikeAndDateRange(@Param("cardId") Long cardId,
                                                        @Param("typePattern") String typePattern,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COALESCE(SUM(tr_fee), 0) FROM transaction_history WHERE tr_sender_id = :cardId AND tr_type LIKE :typePattern AND tr_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    BigDecimal sumFeeBySenderAndTypeLikeAndDateRange(@Param("cardId") Long cardId,
                                                     @Param("typePattern") String typePattern,
                                                     @Param("startDate") LocalDateTime startDate,
                                                     @Param("endDate") LocalDateTime endDate);

    @Query(value = "SELECT COALESCE(SUM(tr_amount), 0) FROM transaction_history WHERE tr_receiver_id = :cardId AND tr_type = :type AND tr_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    BigDecimal sumAmountByReceiverAndTypeAndDateRange(@Param("cardId") Long cardId,
                                                      @Param("type") String type,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionHistory t " +
            "WHERE t.receiverId = :cardId AND t.type IN :types AND t.date >= :startDate AND t.date < :endDate")
    BigDecimal sumIncomingAmountByCardAndTypes(@Param("cardId") Long cardId,
                                               @Param("types") List<String> types,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TransactionHistory t " +
            "WHERE (t.senderId = :cardId OR t.receiverId = :cardId) AND t.type = :type AND t.date >= :startDate AND t.date <= :endDate")
    boolean existsTransactionForCardAndTypeInDateRange(@Param("cardId") Long cardId,
                                                       @Param("type") String type,
                                                       @Param("startDate") LocalDateTime startDate,
                                                       @Param("endDate") LocalDateTime endDate);

    @Modifying
    @Query(value = """
        INSERT INTO transaction_history
            (tr_sender_id, tr_sender_user_id, tr_receiver_id, tr_receiver_user_id, tr_type, tr_amount, tr_fee, tr_date)
        VALUES
            (:senderId, :senderUserId, :receiverId, :receiverUserId, :type, :amount, :fee, :date)
        """, nativeQuery = true)
    void insertTransactionAtDate(
            @Param("senderId") Long senderId,
            @Param("senderUserId") Long senderUserId,
            @Param("receiverId") Long receiverId,
            @Param("receiverUserId") Long receiverUserId,
            @Param("type") String type,
            @Param("amount") BigDecimal amount,
            @Param("fee") BigDecimal fee,
            @Param("date") LocalDateTime date
    );
}
