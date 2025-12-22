package com.bank.banksystem.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {

    private Long userId;
    private String newPassword;
}

