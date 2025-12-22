package com.bank.banksystem.controller;

import com.bank.banksystem.service.ICardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/login")
public class CardController{

    @Autowired
    private ICardService cardService;

    @GetMapping("/{userId}/dashboard")
    public ResponseEntity<List<Object>> displayUserCards(@PathVariable Long userId) {
        List<Object> cards = cardService.getUserCards(userId);
        return ResponseEntity.ok(cards);
    }

    @GetMapping("/{userId}/dashboard/{cardId}")
    public ResponseEntity<String> selectCurrentCard(@PathVariable Long cardId, @PathVariable Long userId) {
        boolean success = cardService.setCurrentCard(cardId, userId);
        if (success) {
            return ResponseEntity.ok("Card set as current successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to set current card");
        }
    }


    @GetMapping("/{userId}/dashboard/{cardId}/details")
    public Object displayCardDetails(@PathVariable Long cardId, @PathVariable Long userId) {
        return cardService.getCardDetails(cardId, userId);
    }
}
