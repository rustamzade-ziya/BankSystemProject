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
    boolean success = Boolean.parseBoolean(paymentService.withdraw(request));

    if (success) {
      return ResponseEntity.ok("Payment successful for service: " + request.getServiceType());
    } else {
      return ResponseEntity.badRequest().body("Payment failed. Check balance or card info.");
    }
  }
}
