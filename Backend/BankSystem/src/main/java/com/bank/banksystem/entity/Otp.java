package com.bank.banksystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data// getters & setters
@Entity
@Table(name = "otp")
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "otp_id")
    private Long otpId;

    @Column(name = "user_email", nullable = false, unique = true)
    private String userEmail;

    @Column(name = "otp_code")
    private Integer otpCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "attempts")
    private Integer attempts;

    @Column(name = "first_attempt_at")
    private LocalDateTime firstAttemptAt;

    @Column(name = "blocked_until")
    private LocalDateTime blockedUntil;
}