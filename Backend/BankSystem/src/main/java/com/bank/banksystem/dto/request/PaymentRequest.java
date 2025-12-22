package com.bank.banksystem.dto.request;

import java.math.BigDecimal;

public class PaymentRequest {
  private Long senderCardId; // card chosen to pay
  private String receiverCode; // random code (just decorative)
  private BigDecimal amount; // amount to withdraw
  private String serviceType; // GAS, WATER, LIGHT

  // getters and setters

  public Long getSenderCardId() {
    return senderCardId;
  }

  public void setSenderCardId(Long senderCardId) {
    this.senderCardId = senderCardId;
  }

  public String getReceiverCode() {
    return receiverCode;
  }

  public void setReceiverCode(String receiverCode) {
    this.receiverCode = receiverCode;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }

  public String getServiceType() {
    return serviceType;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }
}
