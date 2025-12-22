package com.bank.banksystem.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cashback_information")
public class Cashback {
    @Id
    @Column(name = "cb_ID")
    private Long cashbackId;

    @ManyToOne
    @JoinColumn(name = "user_ID", nullable = false)
    private User user;

    @Column(name = "cb_balance")
    private BigDecimal balance;

    // @Column(name = "cb_percent")
    // private BigDecimal cashbackPercent;

    public Cashback(Long cashbackId, User user, BigDecimal balance) {
        this.cashbackId = cashbackId;
        this.user = user;
        this.balance = balance;
        // this.cashbackPercent = cashbackPercent;
    }

    public Cashback() {
    }

    public Long getCashbackId() {
        return cashbackId;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    // public BigDecimal getCashbackPercent() {
    // // return cashbackPercent;
    // }

    public void setCashbackId(Long cashbackId) {
        this.cashbackId = cashbackId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    // public void setCashbackPercent(BigDecimal cashbackPercent) {
    // this.cashbackPercent = cashbackPercent;
    // }
}
