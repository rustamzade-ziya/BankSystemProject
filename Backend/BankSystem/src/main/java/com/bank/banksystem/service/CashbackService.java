package com.bank.banksystem.service;

import com.bank.banksystem.entity.Cashback;
import com.bank.banksystem.entity.DebitCard;
import com.bank.banksystem.entity.User;
import com.bank.banksystem.repository.CashbackRepository;
import com.bank.banksystem.repository.DebitCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Service
public class CashbackService {

    @Autowired
    private CashbackRepository cashbackRepository;

    @Autowired
    private DebitCardRepository debitCardRepository;

    private final Random random = new Random();

    private Long generateRandomCashbackId() {
        Long cashbackId;
        do {
            StringBuilder cashbackIdBuilder = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                cashbackIdBuilder.append(random.nextInt(10));
            }
            cashbackId = Long.parseLong(cashbackIdBuilder.toString());
        } while (cashbackRepository.existsById(cashbackId));

        return cashbackId;
    }

    public void createCashbackForUser(User user) {
        Long cashbackId = generateRandomCashbackId();

        Cashback cashback = new Cashback();
        cashback.setCashbackId(cashbackId);
        cashback.setUser(user);
        cashback.setBalance(BigDecimal.ZERO);

        cashbackRepository.save(cashback);
    }

    @Transactional
    public BigDecimal addCashback(Long userId, Long senderCardId, BigDecimal amount, String serviceType) {

        Optional<DebitCard> debitCardOpt = debitCardRepository.findById(senderCardId);
        if (!debitCardOpt.isPresent()) {
            throw new RuntimeException("Debit card not found with ID: " + senderCardId);
        }

        DebitCard debitCard = debitCardOpt.get();
        String cardType = debitCard.getPpn();

        if (cardType == null) {
            throw new RuntimeException("Card type (PPN) is null");
        }
        BigDecimal percentage = getCashbackPercentage(cardType, serviceType);

        BigDecimal cashbackAmount = amount.multiply(percentage);

        Optional<Cashback> cashbackOpt = cashbackRepository.findByUserId(userId);
        if (!cashbackOpt.isPresent()) {
            throw new RuntimeException("Cashback record not found for user: " + userId);
        }

        cashbackRepository.addToBalance(userId, cashbackAmount);

        return cashbackAmount;
    }

    public BigDecimal getCashbackBalance(Long userId) {
        Optional<Cashback> cashbackOpt = cashbackRepository.findByUserId(userId);
        if (!cashbackOpt.isPresent()) {
            throw new RuntimeException("Cashback record not found for user: " + userId);
        }
        return cashbackOpt.get().getBalance();
    }

    private BigDecimal getCashbackPercentage(String cardType, String serviceType) {
        cardType = cardType.toUpperCase();
        serviceType = serviceType.toUpperCase();

        if ("VISA".equals(cardType)) {
            switch (serviceType) {
                case "GAS":
                    return new BigDecimal("0.01");
                case "WATER":
                    return new BigDecimal("0.015");
                case "ELECTRICITY":
                    return new BigDecimal("0.02");
                default:
                    return BigDecimal.ZERO;
            }
        } else if ("MASTERCARD".equals(cardType)) {
            switch (serviceType) {
                case "GAS":
                    return new BigDecimal("0.02");
                case "WATER":
                    return new BigDecimal("0.01");
                case "ELECTRICITY":
                    return new BigDecimal("0.015");
                default:
                    return BigDecimal.ZERO;
            }
        }

        return BigDecimal.ZERO;
    }
}
