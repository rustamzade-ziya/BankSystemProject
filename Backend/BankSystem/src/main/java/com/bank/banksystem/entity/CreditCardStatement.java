package com.bank.banksystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "credit_card_statements")
public class CreditCardStatement {

    @Id
    @Column(name = "statement_id")
    private String statementId;

    @Column(name = "user_ID")
    private Long userId;

    @Column(name = "c_card_id")
    private Long cardId;

    @Column(name = "statement_date", nullable = false)
    private LocalDate statementDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "opening_balance")
    private BigDecimal openingBalance = BigDecimal.ZERO;

    @Column(name = "closing_balance")
    private BigDecimal closingBalance = BigDecimal.ZERO;

    @Column(name = "min_payment_due")
    private BigDecimal minPaymentDue = BigDecimal.ZERO;

    @Column(name = "total_payment_due")
    private BigDecimal totalPaymentDue = BigDecimal.ZERO;

    @Column(name = "interest_charged")
    private BigDecimal interestCharged = BigDecimal.ZERO;

    @Column(name = "fees_charged")
    private BigDecimal feesCharged = BigDecimal.ZERO;

    @Column(name = "purchases")
    private BigDecimal purchases = BigDecimal.ZERO;

    @Column(name = "payments")
    private BigDecimal payments = BigDecimal.ZERO;

    @Column(name = "status", nullable = false)
    private String status;

    public CreditCardStatement(String statementId, Long userId, Long cardId, LocalDate statementDate, LocalDate dueDate, BigDecimal openingBalance, BigDecimal closingBalance, BigDecimal minPaymentDue, BigDecimal totalPaymentDue, BigDecimal interestCharged, BigDecimal feesCharged, BigDecimal purchases, BigDecimal payments, String status) {
        this.statementId = statementId;
        this.userId = userId;
        this.cardId = cardId;
        this.statementDate = statementDate;
        this.dueDate = dueDate;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.minPaymentDue = minPaymentDue;
        this.totalPaymentDue = totalPaymentDue;
        this.interestCharged = interestCharged;
        this.feesCharged = feesCharged;
        this.purchases = purchases;
        this.payments = payments;
        this.status = status;
    }

    public CreditCardStatement() {

    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public LocalDate getStatementDate() {
        return statementDate;
    }

    public void setStatementDate(LocalDate statementDate) {
        this.statementDate = statementDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(BigDecimal openingBalance) {
        this.openingBalance = openingBalance;
    }

    public BigDecimal getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(BigDecimal closingBalance) {
        this.closingBalance = closingBalance;
    }

    public BigDecimal getMinPaymentDue() {
        return minPaymentDue;
    }

    public void setMinPaymentDue(BigDecimal minPaymentDue) {
        this.minPaymentDue = minPaymentDue;
    }

    public BigDecimal getTotalPaymentDue() {
        return totalPaymentDue;
    }

    public void setTotalPaymentDue(BigDecimal totalPaymentDue) {
        this.totalPaymentDue = totalPaymentDue;
    }

    public BigDecimal getInterestCharged() {
        return interestCharged;
    }

    public void setInterestCharged(BigDecimal interestCharged) {
        this.interestCharged = interestCharged;
    }

    public BigDecimal getFeesCharged() {
        return feesCharged;
    }

    public void setFeesCharged(BigDecimal feesCharged) {
        this.feesCharged = feesCharged;
    }

    public BigDecimal getPurchases() {
        return purchases;
    }

    public void setPurchases(BigDecimal purchases) {
        this.purchases = purchases;
    }

    public BigDecimal getPayments() {
        return payments;
    }

    public void setPayments(BigDecimal payments) {
        this.payments = payments;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}