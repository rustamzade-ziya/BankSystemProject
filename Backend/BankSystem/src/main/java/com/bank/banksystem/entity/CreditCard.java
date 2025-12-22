package com.bank.banksystem.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "credit_card_information")
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "c_card_ID")
    private Long cardId;

    @ManyToOne
    @JoinColumn(name = "user_ID", nullable = false)
    private User user;

    @Column(name = "c_balance")
    private BigDecimal balance;

    @Column(name = "c_loan_amount")
    private BigDecimal loanAmount;

    @Column(name = "c_interest_rate")
    private BigDecimal interestRate;

    @Column(name = "c_current_debt")
    private BigDecimal currentDebt;

    @Column(name = "c_ppn")
    private String ppn;

    @Column(name = "c_currency")
    private String currency;

    @Column(name = "c_cvv")
    private String cvv;

    @Column(name = "c_expiry_date")
    private String expiryDate;

    @Column(name = "c_status")
    private String status;

    @Column(name = "c_pin")
    private String pin;

    public CreditCard(Long cardId, User user, BigDecimal balance, BigDecimal loanAmount, BigDecimal interestRate, String ppn,String currency,String cvv, String expiryDate, String status,  String pin) {
        this.cardId = cardId;
        this.user = user;
        this.balance = balance;
        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.ppn = ppn;
        this.currency = currency;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
        this.status = status;
        this.pin = pin;
    }

    public CreditCard() {

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

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(BigDecimal loanAmount) {
        this.loanAmount = loanAmount;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public String getPpn() {
        return ppn;
    }

    public void setPpn(String ppn) {
        this.ppn = ppn;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrency() { return currency; }

    public BigDecimal getCurrentDebt() {
        return currentDebt;
    }

    public void setCurrentDebt(BigDecimal currentDebt) {
        this.currentDebt = currentDebt;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPin() {
        return pin;
    }
    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
}
