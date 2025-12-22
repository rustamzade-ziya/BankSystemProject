package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.LoginRequest;
import com.bank.banksystem.dto.response.UserResponse;
import com.bank.banksystem.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            UserResponse userResponse = loginService.login(loginRequest);
            return ResponseEntity.ok(userResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); //400 Bad Request
        }
    }
}