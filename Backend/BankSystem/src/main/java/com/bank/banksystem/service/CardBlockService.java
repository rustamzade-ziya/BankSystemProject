package com.bank.banksystem.service;

import com.bank.banksystem.repository.CreditCardRepository;
import com.bank.banksystem.repository.DebitCardRepository;
import org.springframework.stereotype.Service;

@Service
public class CardBlockService {

    private final DebitCardRepository debitRepo;
    private final CreditCardRepository creditRepo;

    public CardBlockService(
            DebitCardRepository debitRepo,
            CreditCardRepository creditRepo
    ) {
        this.debitRepo = debitRepo;
        this.creditRepo = creditRepo;
    }

    // BLOCK CARD
    public void blockCard(Long userId, Long cardId) {

        String debitStatus = debitRepo.getStatus(cardId, userId);
        if (debitStatus != null) {
            debitRepo.updateStatus(cardId, userId, "BLOCKED");
            return;
        }

        String creditStatus = creditRepo.getStatus(cardId, userId);
        if (creditStatus != null) {
            creditRepo.updateStatus(cardId, userId, "BLOCKED");
            return;
        }

        throw new RuntimeException("Card not found or does not belong to user");
    }

    // UNBLOCK CARD
    public void unblockCard(Long userId, Long cardId) {

        String debitStatus = debitRepo.getStatus(cardId, userId);
        if (debitStatus != null) {
            debitRepo.updateStatus(cardId, userId, "ACTIVE");
            return;
        }

        String creditStatus = creditRepo.getStatus(cardId, userId);
        if (creditStatus != null) {
            creditRepo.updateStatus(cardId, userId, "ACTIVE");
            return;
        }

        throw new RuntimeException("Card not found or does not belong to user");
    }
}
