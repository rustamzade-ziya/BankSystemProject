package com.bank.banksystem.repository;

import com.bank.banksystem.entity.Otp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface otpRepository extends JpaRepository<Otp, Long> {

    // найти OTP по email
    Optional<Otp> findByUserEmail(String userEmail);

    // проверить, есть ли OTP для email
    boolean existsByUserEmail(String userEmail);

    // удалить OTP (после успешной проверки)
    void deleteByUserEmail(String userEmail);
}