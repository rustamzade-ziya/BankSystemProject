package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.CreateCreditCardRequest;
import com.bank.banksystem.dto.response.CreateCreditCardResponse;
import com.bank.banksystem.service.CreditCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    @PostMapping("/create")
    public ResponseEntity<CreateCreditCardResponse> createCreditCard(
            @RequestBody CreateCreditCardRequest request) {

        CreateCreditCardResponse response = creditCardService.createCreditCard(request);

        if (response.getCardId() == null) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
