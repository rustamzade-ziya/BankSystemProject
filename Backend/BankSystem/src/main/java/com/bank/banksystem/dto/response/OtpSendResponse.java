package com.bank.banksystem.dto.response;

import lombok.Data;

@Data
public class OtpSendResponse {
  private String message;

  public OtpSendResponse(String message) {
    this.message = message;
  }

}
