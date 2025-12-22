package com.bank.banksystem.repository;

import com.bank.banksystem.entity.Cashback;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CashbackRepository extends JpaRepository<Cashback, Long> {

    // Find cashback by user ID
    @Query(value = "SELECT * FROM cashback_information WHERE user_id = :userId", nativeQuery = true)
    Optional<Cashback> findByUserId(@Param("userId") Long userId);

    // Update cashback balance
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE cashback_information SET cb_balance = :balance WHERE user_id = :userId", nativeQuery = true)
    void updateBalance(@Param("userId") Long userId, @Param("balance") BigDecimal balance);

    // Add to cashback balance
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE cashback_information SET cb_balance = cb_balance + :amount WHERE user_id = :userId", nativeQuery = true)
    void addToBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    // Subtract from cashback balance (use cashback)
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE cashback_information SET cb_balance = cb_balance - :amount WHERE user_id = :userId", nativeQuery = true)
    void subtractFromBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}
