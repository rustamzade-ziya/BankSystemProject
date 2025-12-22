package com.bank.banksystem.repository;

import com.bank.banksystem.entity.CreditCardStatement;
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
public interface CreditCardStatementRepository extends JpaRepository<CreditCardStatement, String> {

    //Get all values by the last statement written in database
    @Query(value = """
        SELECT * FROM credit_card_statements 
        WHERE card_id = :cardId 
        ORDER BY statement_date DESC 
        LIMIT 1
        """, nativeQuery = true)
    Optional<CreditCardStatement> findLatestStatementByCardId(@Param("cardId") Long cardId);

    // Insert the statement
    @Modifying
    @Query(value = """
        INSERT INTO credit_card_statements 
        (statement_id, user_id, card_id, statement_date, due_date, 
         opening_balance, closing_balance, min_payment_due, total_payment_due,
         interest_charged, fees_charged, purchases, payments, status)
        VALUES 
        (:statementId, :userId, :cardId, :statementDate, :dueDate,
         :openingBalance, :closingBalance, :minPaymentDue, :totalPaymentDue,
         :interestCharged, :feesCharged, :purchases, :payments, :status)
        """, nativeQuery = true)
    int insertStatement(
            @Param("statementId") String statementId,
            @Param("userId") Long userId,
            @Param("cardId") Long cardId,
            @Param("statementDate") LocalDate statementDate,
            @Param("dueDate") LocalDate dueDate,
            @Param("openingBalance") BigDecimal openingBalance,
            @Param("closingBalance") BigDecimal closingBalance,
            @Param("minPaymentDue") BigDecimal minPaymentDue,
            @Param("totalPaymentDue") BigDecimal totalPaymentDue,
            @Param("interestCharged") BigDecimal interestCharged,
            @Param("feesCharged") BigDecimal feesCharged,
            @Param("purchases") BigDecimal purchases,
            @Param("payments") BigDecimal payments,
            @Param("status") String status
    );


    // All Statements by Card Id
    @Query(value = """
        SELECT * FROM credit_card_statements 
        WHERE card_id = :cardId 
        ORDER BY statement_date DESC
        """, nativeQuery = true)
    List<CreditCardStatement> findAllStatementsByCardIdOrderByDateDesc(@Param("cardId") Long cardId);

    // Get All Statements according to the required year and month
    @Query(value = """
        SELECT * FROM credit_card_statements 
        WHERE card_id = :cardId 
        AND EXTRACT(YEAR FROM statement_date) = :year 
        AND EXTRACT(MONTH FROM statement_date) = :month
        """, nativeQuery = true)
    Optional<CreditCardStatement> findStatementByCardIdAndMonthYear(
            @Param("cardId") Long cardId,
            @Param("year") int year,
            @Param("month") int month
    );

    // Update the Statement Status
    @Modifying
    @Query("UPDATE CreditCardStatement s SET s.status = :status WHERE s.statementId = :statementId")
    int updateStatementStatus(@Param("statementId") String statementId, @Param("status") String status);

    // Find Overdue Statements
    @Query(value = """
        SELECT * FROM credit_card_statements 
        WHERE status = 'OVERDUE' 
        AND due_date < CURRENT_DATE
        """, nativeQuery = true)
    List<CreditCardStatement> findOverdueStatements();

    // Delete all statements by Card Id
    @Modifying
    @Query("DELETE FROM CreditCardStatement s WHERE s.cardId = :cardId")
    int deleteAllStatementsByCardId(@Param("cardId") Long cardId);

    // Statement counter by Card Id
    @Query("SELECT COUNT(s) FROM CreditCardStatement s WHERE s.cardId = :cardId")
    int countStatementsByCardId(@Param("cardId") Long cardId);

    // Get minimum payment by Card Id
    @Query(value = """
        SELECT min_payment_due FROM credit_card_statements 
        WHERE card_id = :cardId 
        ORDER BY statement_date DESC 
        LIMIT 1
        """, nativeQuery = true)
    Optional<BigDecimal> getLatestStatementMinPayment(@Param("cardId") Long cardId);

    // Get latest Closing balance by Card Id
    @Query(value = """
        SELECT closing_balance FROM credit_card_statements 
        WHERE card_id = :cardId 
        ORDER BY statement_date DESC 
        LIMIT 1
        """, nativeQuery = true)
    Optional<BigDecimal> getLatestStatementClosingBalance(@Param("cardId") Long cardId);

    // Search for Statement by Status, Start Date and End Date
    @Query(value = """
        SELECT * FROM credit_card_statements 
        WHERE status = :status 
        AND statement_date BETWEEN :startDate AND :endDate
        """, nativeQuery = true)
    List<CreditCardStatement> findStatementByStatusAndDate(
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}