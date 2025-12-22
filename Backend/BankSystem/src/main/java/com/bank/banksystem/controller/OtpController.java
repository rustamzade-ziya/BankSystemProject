package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.*;
import com.bank.banksystem.dto.response.*;
import com.bank.banksystem.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;
    private final EmailService emailService;

    public OtpController(OtpService otpService, EmailService emailService) {
        this.otpService = otpService;
        this.emailService = emailService;
    }

    // 📤 отправка OTP
    @PostMapping("/send")
    public ResponseEntity<OtpSendResponse> sendOtp(
            @RequestBody OtpSendRequest request
    ) {
        String email = request.getEmail();

        int otp = otpService.sendOtp(email);

        emailService.sendOtpEmail(email, otp);

        return ResponseEntity.ok(
                new OtpSendResponse("OTP sent to email")
        );
    }

    // ✅ проверка OTP
    @PostMapping("/verify")
    public ResponseEntity<OtpVerifyResponse> verifyOtp(
            @RequestBody OtpVerifyRequest request
    ) {
        boolean result = otpService.verifyOtp(
                request.getEmail(),
                request.getOtpCode()
        );

        if (result) {
            return ResponseEntity.ok(
                    new OtpVerifyResponse(true, "OTP verified successfully")
            );
        } else {
            return ResponseEntity.ok(
                    new OtpVerifyResponse(false, "Invalid OTP")
            );
        }
    }
}