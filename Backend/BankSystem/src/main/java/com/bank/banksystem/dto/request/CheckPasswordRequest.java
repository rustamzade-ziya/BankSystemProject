package com.bank.banksystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckPasswordRequest {

    private Long userId;
    private String currentPassword;
}

