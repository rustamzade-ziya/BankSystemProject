package com.bank.banksystem.service;

import com.bank.banksystem.repository.CreditCardRepository;
import com.bank.banksystem.repository.DebitCardRepository;
import com.bank.banksystem.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ResetPinService {

    private final DebitCardRepository debitRepo;
    private final CreditCardRepository creditRepo;
    private final UserRepository userRepo;

    public ResetPinService(
            DebitCardRepository debitRepo,
            CreditCardRepository creditRepo,
            UserRepository userRepo
    ) {
        this.debitRepo = debitRepo;
        this.creditRepo = creditRepo;
        this.userRepo = userRepo;
    }

    // STEP 1 — check current PIN
    public boolean checkPin(Long userId, Long cardId, String pin) {

        String debitPin = debitRepo.getPinByCardId(cardId, userId);
        if (debitPin != null) {
            return debitPin.equals(pin);
        }

        String creditPin = creditRepo.getPinByCardId(cardId, userId);
        if (creditPin != null) {
            return creditPin.equals(pin);
        }

        throw new RuntimeException("Card not found");
    }

    // STEP 2 — check codeword
    public boolean checkCodeword(Long userId, String codeword) {
        String stored = userRepo.getCodewordByUserId(userId);

        if (stored == null) {
            throw new RuntimeException("User not found");
        }

        return stored.equalsIgnoreCase(codeword);
    }

    // STEP 3 — update PIN
    public void updatePin(Long userId, Long cardId, String newPin) {

        if (!newPin.matches("\\d{4}")) {
            throw new RuntimeException("PIN must be exactly 4 digits");
        }

        if (debitRepo.getPinByCardId(cardId, userId) != null) {
            debitRepo.updatePin(cardId, userId, newPin);
            return;
        }

        if (creditRepo.getPinByCardId(cardId, userId) != null) {
            creditRepo.updatePin(cardId, userId, newPin);
            return;
        }

        throw new RuntimeException("Card not found");
    }
}
