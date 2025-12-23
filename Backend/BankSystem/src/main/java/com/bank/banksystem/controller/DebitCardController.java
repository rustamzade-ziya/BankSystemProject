package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.CreateDebitCardRequest;
import com.bank.banksystem.dto.response.CreateDebitCardResponse;
import com.bank.banksystem.service.DebitCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// {
//     "userId": 3,
//     "ppn": "AZE",
//     "currency": "AZN",
//     "pin": "1234"   
// }

@RestController
@RequestMapping("/api/debit-cards")
public class DebitCardController {

    @Autowired
    private DebitCardService debitCardService;

    @PostMapping("/create")
    public ResponseEntity<CreateDebitCardResponse> createDebitCard(
            @RequestBody CreateDebitCardRequest request) {

        CreateDebitCardResponse response = debitCardService.createDebitCard(request);

        if (response.getCardId() == null) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }
}
