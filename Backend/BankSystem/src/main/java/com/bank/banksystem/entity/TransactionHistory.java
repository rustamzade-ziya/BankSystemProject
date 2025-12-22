package com.bank.banksystem.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_history")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tr_id")
    private Long id;

    @Column(name = "tr_sender_id")
    private Long senderId;

    @Column(name = "tr_sender_user_id")
    private Long senderUserId;

    @Column(name = "tr_receiver_user_id")
    private Long receiverUserId;

    @Column(name = "tr_receiver_id")
    private Long receiverId;

    @Column(name = "tr_type")
    private String type;

    // Sender amount (as you wanted)
    @Column(name = "tr_amount")
    private BigDecimal amount;

    // ✅ NEW
    @Column(name = "tr_converted_amount")
    private BigDecimal convertedAmount;

    // ✅ NEW
    @Column(name = "tr_sender_currency")
    private String senderCurrency;

    // ✅ NEW
    @Column(name = "tr_receiver_currency")
    private String receiverCurrency;

    @Column(name = "tr_fee")
    private BigDecimal fee;

    @Column(name = "tr_date", nullable = false)
    private LocalDateTime date;

    public TransactionHistory() {
        this.date = LocalDateTime.now();
    }

    public TransactionHistory(
            Long senderId,
            Long senderUserId,
            Long receiverId,
            Long receiverUserId,
            String type,
            BigDecimal amount,
            BigDecimal convertedAmount,
            BigDecimal fee,
            String senderCurrency,
            String receiverCurrency
    ) {
        this.senderId = senderId;
        this.senderUserId = senderUserId;
        this.receiverId = receiverId;
        this.receiverUserId = receiverUserId;
        this.type = type;
        this.amount = amount;
        this.convertedAmount = convertedAmount;
        this.fee = fee;
        this.senderCurrency = senderCurrency;
        this.receiverCurrency = receiverCurrency;
        this.date = LocalDateTime.now();
    }

    // getters & setters (ADD for new fields)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(BigDecimal convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public String getSenderCurrency() {
        return senderCurrency;
    }

    public void setSenderCurrency(String senderCurrency) {
        this.senderCurrency = senderCurrency;
    }

    public String getReceiverCurrency() {
        return receiverCurrency;
    }

    public void setReceiverCurrency(String receiverCurrency) {
        this.receiverCurrency = receiverCurrency;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
