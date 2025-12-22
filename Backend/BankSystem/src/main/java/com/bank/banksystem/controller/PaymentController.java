package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.PaymentRequest;
import com.bank.banksystem.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public ResponseEntity<String> payForService(@RequestBody PaymentRequest request) {
        String result = paymentService.withdraw(request);

        if (result.startsWith("Payment successful")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}
