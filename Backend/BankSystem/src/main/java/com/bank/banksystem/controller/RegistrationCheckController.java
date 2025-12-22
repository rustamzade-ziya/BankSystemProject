package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.RegistrationCheckRequest;
import com.bank.banksystem.dto.response.RegistrationCheckResponse;
import com.bank.banksystem.service.RegistrationCheckService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registration")
public class RegistrationCheckController {

    private final RegistrationCheckService registrationCheckService;

    public RegistrationCheckController(RegistrationCheckService registrationCheckService) {
        this.registrationCheckService = registrationCheckService;
    }

    @PostMapping("/check")
    public RegistrationCheckResponse checkRegistrationInfo(
            @RequestBody RegistrationCheckRequest request
    ) {
        return registrationCheckService.checkUniqueFields(request);
    }
}
