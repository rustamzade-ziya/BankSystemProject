package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.CheckPinRequest;
import com.bank.banksystem.dto.request.CheckCodewordRequest;
import com.bank.banksystem.dto.request.UpdatePinRequest;
import com.bank.banksystem.service.ResetPinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ResetPinController {

    private final ResetPinService service;

    public ResetPinController(ResetPinService service) {
        this.service = service;
    }

    @PostMapping("/{userId}/dashboard/{cardId}/reset-pin")
    public ResponseEntity<Boolean> checkPin(
            @PathVariable Long userId,
            @PathVariable Long cardId,
            @RequestBody CheckPinRequest request
    ) {
        return ResponseEntity.ok(
                service.checkPin(userId, cardId, request.getPin())
        );
    }

    @PostMapping("/{userId}/dashboard/{cardId}/reset-pin/codeword/check-codeword")
    public ResponseEntity<Boolean> checkCodeword(
            @PathVariable Long userId,
            @RequestBody CheckCodewordRequest request
    ) {
        return ResponseEntity.ok(
                service.checkCodeword(userId, request.getCodeword())
        );
    }

    @PostMapping("/{userId}/dashboard/{cardId}/reset-pin/new-pin/update-pin")
    public ResponseEntity<Void> updatePin(
            @PathVariable Long userId,
            @PathVariable Long cardId,
            @RequestBody UpdatePinRequest request
    ) {
        service.updatePin(userId, cardId, request.getNewPin());
        return ResponseEntity.ok().build();
    }
}
