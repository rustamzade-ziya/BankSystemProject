package com.bank.banksystem.repository;

import com.bank.banksystem.entity.DebitCard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface DebitCardRepository extends JpaRepository<DebitCard, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO debit_card_information (d_card_ID, user_ID, d_balance, d_ppn, d_currency, d_cvv, d_expiry_date, d_status, d_pin) " +
            "VALUES (:cardId, :userId, :balance, :ppn, :currency, :cvv, :expiry_date, :status, :pin)",
            nativeQuery = true)
    void insertCard(
            @Param("cardId") Long cardId,
            @Param("userId") Long userId,
            @Param("balance") Double balance,
            @Param("ppn") String ppn,
            @Param("currency") String currency,
            @Param("cvv") String cvv,
            @Param("expiry_date") String expiry_date,
            @Param("status") String status,
            @Param("pin") String pin
    );

    // Check if card ID exists in debit cards
    @Query(
            value = "SELECT COUNT(*) > 0 FROM debit_card_information WHERE d_card_id = :cardId",
            nativeQuery = true
    )
    boolean existsByCardId(@Param("cardId") Long cardId);

    // Check if card ID exists in credit cards
    @Query(
            value = "SELECT COUNT(*) > 0 FROM credit_card_information WHERE c_card_id = :cardId",
            nativeQuery = true
    )
    boolean existsInCreditCards(@Param("cardId") Long cardId);

    // Find debit card by card ID
    @Query(
            value = "SELECT * FROM debit_card_information WHERE d_card_id = :cardId",
            nativeQuery = true
    )
    Optional<DebitCard> findByCardId(@Param("cardId") Long cardId);

    // Find all debit cards for user
    @Query(
            value = "SELECT * FROM debit_card_information WHERE user_id = :userId",
            nativeQuery = true
    )
    List<DebitCard> findByUserId(@Param("userId") Long userId);

    // Find active debit cards for user
    @Query(
            value = "SELECT * FROM debit_card_information WHERE user_id = :userId AND d_status = 'ACTIVE'",
            nativeQuery = true
    )
    List<DebitCard> findActiveCardsByUserId(@Param("userId") Long userId);

    // Change debit card balance entirely
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE debit_card_information SET d_balance = :balance WHERE d_card_id = :cardId",
            nativeQuery = true
    )
    void changeBalanceEntirely(@Param("cardId") Long cardId, @Param("balance") BigDecimal balance);

    // Add to debit card balance
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE debit_card_information SET d_balance = d_balance + :amount WHERE d_card_id = :cardId",
            nativeQuery = true
    )
    void addToBalance(@Param("cardId") Long cardId, @Param("amount") BigDecimal amount);

    // Subtract from debit card balance
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE debit_card_information SET d_balance = d_balance - :amount WHERE d_card_id = :cardId",
            nativeQuery = true
    )
    void subtractFromBalance(@Param("cardId") Long cardId, @Param("amount") BigDecimal amount);

    // Change card status
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE debit_card_information SET d_status = :status WHERE d_card_id = :cardId",
            nativeQuery = true
    )
    void updateStatus(@Param("cardId") Long cardId, @Param("status") String status);

    @Query(
            value = "SELECT d_pin FROM debit_card_information WHERE d_card_id = :cardId AND user_id = :userId",
            nativeQuery = true
    )
    String getPinByCardId(@Param("cardId") Long cardId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE debit_card_information SET d_pin = :pin WHERE d_card_id = :cardId AND user_id = :userId",
            nativeQuery = true
    )
    void updatePin(
            @Param("cardId") Long cardId,
            @Param("userId") Long userId,
            @Param("pin") String pin
    );

    @Query(
            value = "SELECT d_status FROM debit_card_information WHERE d_card_id = :cardId AND user_id = :userId",
            nativeQuery = true
    )
    String getStatus(@Param("cardId") Long cardId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE debit_card_information SET d_status = :status WHERE d_card_id = :cardId AND user_id = :userId",
            nativeQuery = true
    )
    void updateStatus(
            @Param("cardId") Long cardId,
            @Param("userId") Long userId,
            @Param("status") String status
    );

    @Query(
            value = "SELECT user_id FROM debit_card_information WHERE d_card_id = :cardId",
            nativeQuery = true
    )
    Long getIdbyCardId(@Param("cardId") Long cardId);
}