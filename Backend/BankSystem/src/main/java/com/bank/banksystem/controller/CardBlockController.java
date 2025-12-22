package com.bank.banksystem.controller;

import com.bank.banksystem.service.CardBlockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CardBlockController {

    private final CardBlockService service;

    public CardBlockController(CardBlockService service) {
        this.service = service;
    }

    @PostMapping("/{userId}/dashboard/{cardId}/block")
    public ResponseEntity<Void> blockCard(
            @PathVariable Long userId,
            @PathVariable Long cardId
    ) {
        service.blockCard(userId, cardId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/dashboard/{cardId}/unlock")
    public ResponseEntity<Void> unblockCard(
            @PathVariable Long userId,
            @PathVariable Long cardId
    ) {
        service.unblockCard(userId, cardId);
        return ResponseEntity.ok().build();
    }
}
