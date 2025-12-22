// src/main/java/com/bank/banksystem/repository/CreditCardStatementRepository.java
package com.bank.banksystem.repository;

import com.bank.banksystem.entity.CreditCardStatement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardStatementRepository extends JpaRepository<CreditCardStatement, String> {

    @Query(value = """
        SELECT * FROM credit_card_statements
        WHERE c_card_id = :cardId
        ORDER BY statement_date DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<CreditCardStatement> findLatestStatementByCardId(@Param("cardId") Long cardId);

    @Query(value = """
        SELECT * FROM credit_card_statements
        WHERE c_card_id = :cardId
        ORDER BY statement_date DESC
        """, nativeQuery = true)
    List<CreditCardStatement> findAllStatementsByCardIdOrderByDateDesc(@Param("cardId") Long cardId);

    @Query(value = """
        SELECT * FROM credit_card_statements
        WHERE c_card_id = :cardId AND status = 'OPEN'
        ORDER BY statement_date DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<CreditCardStatement> findLatestOpenStatementByCardId(@Param("cardId") Long cardId);

    @Query(value = """
        SELECT * FROM credit_card_statements
        WHERE status = 'OPEN'
          AND statement_date <= :today
        ORDER BY statement_date ASC
        """, nativeQuery = true)
    List<CreditCardStatement> findOpenStatementsToFinalize(@Param("today") LocalDate today);

    @Query(value = """
        SELECT * FROM credit_card_statements
        WHERE status = 'BILLED'
          AND due_date <= :today
        ORDER BY due_date ASC
        """, nativeQuery = true)
    List<CreditCardStatement> findBilledStatementsToProcessDue(@Param("today") LocalDate today);

    @Query(value = """
        SELECT * FROM credit_card_statements
        WHERE status IN ('OVERDUE', 'OVERDUE_MIN_PAID')
        """, nativeQuery = true)
    List<CreditCardStatement> findAllOverdueLikeStatements();

    @Query(value = """
        SELECT * FROM credit_card_statements
        WHERE c_card_id = :cardId
          AND status IN ('BILLED', 'OVERDUE', 'OVERDUE_MIN_PAID')
        ORDER BY statement_date DESC
        LIMIT 1
        """, nativeQuery = true)
    Optional<CreditCardStatement> findLatestPayableStatementByCardId(@Param("cardId") Long cardId);


    /**
     * Needed by CreditCardStatementService.registerRepayment()
     * Returns statements that may accept repayments.
     * We sort by due_date ASC so the oldest payable statement is first.
     */
    @Query(value = """
        SELECT * FROM credit_card_statements
        WHERE c_card_id = :cardId
          AND status IN ('BILLED', 'OVERDUE', 'OVERDUE_MIN_PAID')
        ORDER BY due_date ASC
        """, nativeQuery = true)
    List<CreditCardStatement> findPayableStatementsByCardIdOrderByDueDateAsc(@Param("cardId") Long cardId);


    @Modifying
    @Query(value = """
        UPDATE credit_card_statements
        SET status = 'PAID'
        WHERE c_card_id = :cardId
          AND status IN ('BILLED', 'OVERDUE', 'OVERDUE_MIN_PAID')
        """, nativeQuery = true)
    void markAllPayableStatementsPaid(@Param("cardId") Long cardId);
}
