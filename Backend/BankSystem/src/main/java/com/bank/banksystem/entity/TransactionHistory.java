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

    @Column(name = "tr_amount")
    private BigDecimal amount;

    @Column(name = "tr_fee")
    private BigDecimal fee;

    @Column(name = "tr_date", nullable = false)
    private LocalDateTime date;

    // === Constructors ===
    public TransactionHistory() {
        this.date = LocalDateTime.now(); // по умолчанию текущий timestamp
    }

    public TransactionHistory(Long senderId, Long senderUserId, Long receiverId, Long receiverUserId, String type,
                              BigDecimal amount) {
        this.senderId = senderId;
        this.senderUserId = senderUserId;
        this.receiverId = receiverId;
        this.receiverUserId = receiverUserId;
        this.type = type;
        this.amount = amount;
        this.date = LocalDateTime.now();
    }

    // === Getters & Setters ===
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

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getReceiverUserId() {
        return receiverUserId;
    }

    public void setReceiverUserId(Long receiverUserId) {
        this.receiverUserId = receiverUserId;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }
}
