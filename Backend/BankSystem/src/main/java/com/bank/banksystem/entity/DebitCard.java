package com.bank.banksystem.entity;

import com.bank.banksystem.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.*;

import java.math.BigDecimal;
@Entity
@Table(name = "debit_card_information")
public class DebitCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "d_card_ID")
    private Long cardId;

    @ManyToOne
    @JoinColumn(name = "user_ID", nullable = false)
    private User user;

    //big decimal - type used for financial calculations
    @Column(name = "d_balance")
    private BigDecimal balance;

    @Column (name = "d_ppn")
    private String ppn;

    @Column (name = "d_currency")
    private String d_currency;

    @Column(name = "d_cvv")
    private String d_cvv;

    @Column(name = "d_expiry_date")
    private String d_expiry_date;

    @Column(name = "d_status")
    private String d_status;

    @Column(name = "d_pin")
    private String d_pin;

    public DebitCard(Long cardId, User user, BigDecimal balance, String ppn, String d_currency, String d_cvv, String d_expiry_date, String d_status,  String d_pin) {
        this.cardId = cardId;
        this.user = user;
        this.balance = balance;
        this.ppn = ppn;
        this.d_currency = d_currency;
        this.d_cvv = d_cvv;
        this.d_expiry_date = d_expiry_date;
        this.d_status = d_status;
        this.d_pin = d_pin;
    }

    public DebitCard() {

    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPpn() {
        return ppn;
    }

    public void setPpn(String ppn) {
        this.ppn = ppn;
    }

    public String getD_currency() {
        return d_currency;
    }

    public void setD_currency(String d_currency) {
        this.d_currency = d_currency;
    }

    public String getD_cvv() {
        return d_cvv;
    }

    public void setD_cvv(String d_cvv) {
        this.d_cvv = d_cvv;
    }

    public String getD_expiry_date() {
        return d_expiry_date;
    }

    public void setD_expiry_date(String d_expiry_date) {
        this.d_expiry_date = d_expiry_date;
    }

    public String getD_status() {
        return d_status;
    }

    public void setD_status(String d_status) {
        this.d_status = d_status;
    }

    public String getD_pin() {
        return d_pin;
    }

    public void setD_pin(String d_pin) {}
}