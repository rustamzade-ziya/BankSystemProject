package com.bank.banksystem.dto.request;

import lombok.Data;

@Data// @Data = getters,setters(Lombok)
public class OtpSendRequest {
    private String email;

}