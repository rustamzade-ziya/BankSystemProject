package com.bank.banksystem.service;

import com.bank.banksystem.entity.Cashback;
import com.bank.banksystem.entity.CreditCard;
import com.bank.banksystem.entity.DebitCard;

import java.util.List;

public interface ICardService {

    public List<Object> getUserCards(Long userId); // Returns all cards

    public DebitCard getDebitCard(Long cardId);

    public CreditCard getCreditCard(Long cardId);

    public Cashback getCashbackCard(Long userId);

    public boolean setCurrentCard(Long cardId, Long userId);

    public Object getCardDetails(Long cardId, Long userId);
}
