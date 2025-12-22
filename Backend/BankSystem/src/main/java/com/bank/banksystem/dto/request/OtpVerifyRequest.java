package com.bank.banksystem.dto.request;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String email;
    private int otpCode;

}
