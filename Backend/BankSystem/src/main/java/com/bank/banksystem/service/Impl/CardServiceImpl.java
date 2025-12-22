package com.bank.banksystem.service.Impl;

import com.bank.banksystem.dto.response.CardDashboardResponse;
import com.bank.banksystem.dto.response.CashbackCardDashboardDetailsResponse;
import com.bank.banksystem.dto.response.CreditCardDashboardDetailsResponse;
import com.bank.banksystem.dto.response.DebitCardDashboardDetailsResponse;
import com.bank.banksystem.entity.Cashback;
import com.bank.banksystem.entity.CreditCard;
import com.bank.banksystem.entity.DebitCard;
import com.bank.banksystem.repository.CashbackRepository;
import com.bank.banksystem.repository.CreditCardRepository;
import com.bank.banksystem.repository.DebitCardRepository;
import com.bank.banksystem.service.ICardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements ICardService {
    @Autowired
    private DebitCardRepository debitCardRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private CashbackRepository cashbackRepository;

    @Override
    public List<Object> getUserCards(Long userId) {
        List<Object> allCards = new ArrayList<>();

        List<DebitCard> debitCards = debitCardRepository.findByUserId(userId);
        for (DebitCard debitCard : debitCards) {
            CardDashboardResponse cdResponse = new CardDashboardResponse();
            BeanUtils.copyProperties(debitCard, cdResponse);
            allCards.add(cdResponse);
        }

        List<CreditCard> creditCards = creditCardRepository.findByUserId(userId);
        for (CreditCard creditCard : creditCards) {
            CardDashboardResponse cdResponse = new CardDashboardResponse();
            BeanUtils.copyProperties(creditCard, cdResponse);
            allCards.add(cdResponse);
        }

        Optional<Cashback> cashbackCard = cashbackRepository.findByUserId(userId);
        if (cashbackCard.isPresent()) {
            CardDashboardResponse cdResponse = new CardDashboardResponse();
            BeanUtils.copyProperties(cashbackCard.get(), cdResponse);
            cdResponse.setCardId(cashbackCard.get().getCashbackId());
            allCards.add(cdResponse);
        }

        return allCards;
    }

    @Override
    public DebitCard getDebitCard(Long cardId) {
        DebitCard card = debitCardRepository.findByCardId(cardId)
                .orElseThrow(() -> new RuntimeException("Debit card not found"));

        if (card.getD_status() != null && card.getD_status().equalsIgnoreCase("BLOCKED")) {
            throw new RuntimeException("This debit card is blocked");
        }

        return card;
    }

    @Override
    public CreditCard getCreditCard(Long cardId) {
        CreditCard card = creditCardRepository.findByCardId(cardId)
                .orElseThrow(() -> new RuntimeException("Credit card not found"));

        if (card.getStatus() != null && card.getStatus().equalsIgnoreCase("BLOCKED")) {
            throw new RuntimeException("This credit card is blocked");
        }

        return card;
    }

    @Override
    public Cashback getCashbackCard(Long userId) {
        Cashback card = cashbackRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cashback card not found"));

        return card;
    }

    @Override
    public boolean setCurrentCard(Long cardId, Long userId) {
        for (DebitCard debitCard : debitCardRepository.findByUserId(userId)) {
            if (debitCard.getCardId().equals(cardId)) {
                return true;
            }
        }

        for (CreditCard creditCard : creditCardRepository.findByUserId(userId)) {
            if (creditCard.getCardId().equals(cardId)) {
                return true;
            }
        }

        Optional<Cashback> cashback = cashbackRepository.findByUserId(userId);
        if (cashback.isPresent() && cashback.get().getCashbackId().equals(cardId)) {
            return true;
        }

        throw new RuntimeException("Card not found for user or does not belong to user");
    }

    public Object getCardDetails(Long cardId, Long userId) {
        try {
            DebitCardDashboardDetailsResponse debitResp = new DebitCardDashboardDetailsResponse();
            DebitCard debitCard = debitCardRepository.findByCardId(cardId).orElse(null);
            if (debitCard != null && debitCard.getUser().getUser_id().equals(userId)) {
                BeanUtils.copyProperties(debitCard, debitResp);
                debitResp.setCurrency(debitCard.getD_currency());
                debitResp.setCvv(debitCard.getD_cvv());
                debitResp.setExpiry_date(debitCard.getD_expiry_date());
                debitResp.setStatus(debitCard.getD_status());
                return debitResp;
            }
        } catch (Exception ignored) {
        }

        try {
            CreditCardDashboardDetailsResponse creditResp = new CreditCardDashboardDetailsResponse();
            CreditCard creditCard = creditCardRepository.findByCardId(cardId).orElse(null);
            if (creditCard != null && creditCard.getUser().getUser_id().equals(userId)) {
                BeanUtils.copyProperties(creditCard, creditResp);
                creditResp.setCardId(String.valueOf(creditCard.getCardId()));
                return creditResp;
            }
        } catch (Exception ignored) {
        }

        try {
            CashbackCardDashboardDetailsResponse cashbackResp = new CashbackCardDashboardDetailsResponse();
            Cashback cashback = cashbackRepository.findByUserId(userId).orElse(null);
            if (cashback != null) {
                BeanUtils.copyProperties(cashback, cashbackResp);
                cashbackResp.setCardId(cashback.getCashbackId());
                return cashbackResp;
            }
        } catch (Exception ignored) {
        }

        throw new RuntimeException("Card not found or does not belong to this user");
    }

}
