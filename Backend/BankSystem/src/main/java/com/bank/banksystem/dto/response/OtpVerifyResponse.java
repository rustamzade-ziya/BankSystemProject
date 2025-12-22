package com.bank.banksystem.dto.response;

import lombok.Data;

@Data
public class OtpVerifyResponse {
  private boolean success;
  private String message;

  public OtpVerifyResponse(boolean success, String message) {
    this.success = success;
    this.message = message;
  }
}
