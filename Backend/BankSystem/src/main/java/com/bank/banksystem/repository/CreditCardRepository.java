package com.bank.banksystem.repository;

import com.bank.banksystem.entity.CreditCard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO credit_card_information (c_card_ID, user_ID, c_balance, c_loan_amount,c_interest_rate,c_ppn,c_currency,c_cvv,c_expiry_date,c_status, c_pin) " +
            "VALUES (:cardId, :userId, :balance, :loan_amount, :interest_rate, :ppn, :currency, :cvv, :expiry_date, :status, :pin)",
            nativeQuery = true)
    void insertCard(
            @Param("cardId") Long cardId,
            @Param("userId") Long userId,
            @Param("balance") Double balance,
            @Param("loan_amount") Double loan_amount,
            @Param("interest_rate") Float interest_rate,
            @Param("ppn") String ppn,
            @Param("currency") String currency,
            @Param("cvv") String cvv,
            @Param("expiry_date") String expiry_date,
            @Param("status") String status,
            @Param("pin") String pin
    );

    // Check if card ID exists in credit cards
    @Query(
            value = "SELECT COUNT(*) > 0 FROM credit_card_information WHERE c_card_id = :cardId",
            nativeQuery = true
    )
    boolean existsByCardId(@Param("cardId") Long cardId);

    // Check if card ID exists in debit cards
    @Query(
            value = "SELECT COUNT(*) > 0 FROM debit_card_information WHERE d_card_id = :cardId",
            nativeQuery = true
    )
    boolean existsInDebitCards(@Param("cardId") Long cardId);

    // Find credit card by card ID
    @Query(
            value = "SELECT * FROM credit_card_information WHERE c_card_id = :cardId",
            nativeQuery = true
    )
    Optional<CreditCard> findByCardId(@Param("cardId") Long cardId);

    // Find all credit cards for user
    @Query(
            value = "SELECT * FROM credit_card_information WHERE user_id = :userId",
            nativeQuery = true
    )
    List<CreditCard> findByUserId(@Param("userId") Long userId);

    // Find active credit cards for user
    @Query(
            value = "SELECT * FROM credit_card_information WHERE user_id = :userId AND c_status = 'ACTIVE'",
            nativeQuery = true
    )
    List<CreditCard> findActiveCardsByUserId(@Param("userId") Long userId);

    // Update credit card balance
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE credit_card_information SET c_balance = :balance WHERE c_card_id = :cardId",
            nativeQuery = true
    )
    void updateBalance(@Param("cardId") Long cardId, @Param("balance") BigDecimal balance);

    // Subtract from balance (make payment) - исправлена опечатка в названии столбца
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE credit_card_information SET c_balance = c_balance - :amount WHERE c_card_id = :cardId",
            nativeQuery = true
    )
    void subtractFromBalance(@Param("cardId") Long cardId, @Param("amount") BigDecimal amount);

    // Change card status
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(
            value = "UPDATE credit_card_information SET c_status = :status WHERE c_card_id = :cardId",
            nativeQuery = true
    )
    void updateStatus(@Param("cardId") Long cardId, @Param("status") String status);

    @Query(
            value = "SELECT c_pin FROM credit_card_information WHERE c_card_id = :cardId AND user_id = :userId",
            nativeQuery = true
    )
    String getPinByCardId(@Param("cardId") Long cardId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE credit_card_information SET c_pin = :pin WHERE c_card_id = :cardId AND user_id = :userId",
            nativeQuery = true
    )
    void updatePin(
            @Param("cardId") Long cardId,
            @Param("userId") Long userId,
            @Param("pin") String pin
    );

    @Query(
            value = "SELECT c_status FROM credit_card_information WHERE c_card_id = :cardId AND user_id = :userId",
            nativeQuery = true
    )
    String getStatus(@Param("cardId") Long cardId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(
            value = "UPDATE credit_card_information SET c_status = :status WHERE c_card_id = :cardId AND user_id = :userId",
            nativeQuery = true
    )
    void updateStatus(
            @Param("cardId") Long cardId,
            @Param("userId") Long userId,
            @Param("status") String status
    );

    @Query(
            value = "SELECT user_id FROM credit_card_information WHERE c_card_id = :cardId",
            nativeQuery = true
    )
    Long getIdbyCardId(@Param("cardId") Long cardId);
}