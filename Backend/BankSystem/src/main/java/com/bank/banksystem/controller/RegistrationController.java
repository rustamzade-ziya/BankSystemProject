package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.RegistrationRequest;
import com.bank.banksystem.dto.response.UserResponse;
import com.bank.banksystem.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//    {
//        "user_email": "salam.aletkum208@gmail.com",
//            "user_password": 123,
//            "user_name": "Ziya",
//            "user_surname": "Rustamzade",
//            "user_birthday": "2007-04-25",
//            "user_salary": 500000,
//            "user_id_card_no_series": "AA",
//            "user_id_card_no": "1234567800",
//            "user_fin": "abc5678",
//            "user_phone_number": "+994502080000",
//            "user_codeword": "Santa"
//    }

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest registrationRequest) {
        try {
            UserResponse userResponse = registrationService.register(registrationRequest);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

