package com.bank.banksystem.service;

import com.bank.banksystem.dto.request.CreateDebitCardRequest;
import com.bank.banksystem.dto.response.CreateDebitCardResponse;
import com.bank.banksystem.repository.DebitCardRepository;
import com.bank.banksystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class DebitCardService {

    @Autowired
    private DebitCardRepository debitCardRepository;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();

    public CreateDebitCardResponse createDebitCard(CreateDebitCardRequest request) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(request.getUserId())) {
            return new CreateDebitCardResponse(null, null, null, null, "User not found");
        }

        // Генерируем уникальный cardId
        Long cardId;
        do {
            cardId = generateRandomCardId();
        } while (debitCardRepository.existsByCardId(cardId) ||
                debitCardRepository.existsInCreditCards(cardId));

        // Генерируем CVV
        String cvv = generateRandomCVV();

        // Генерируем expiry date (текущая дата + 5 лет)
        String expiryDate = generateExpiryDate();

        // Создаем карту в базе данных
        try {
            debitCardRepository.insertCard(
                    cardId,
                    request.getUserId(),
                    0.0, // начальный баланс
                    request.getPpn(),
                    request.getCurrency(),
                    cvv,
                    expiryDate,
                    "ACTIVE",
                    request.getPin()
            );

            return new CreateDebitCardResponse(
                    cardId,
                    cvv,
                    expiryDate,
                    "ACTIVE",
                    "Debit card created successfully"
            );
        } catch (Exception e) {
            return new CreateDebitCardResponse(
                    null,
                    null,
                    null,
                    null,
                    "Failed to create debit card: " + e.getMessage()
            );
        }
    }

    Long generateRandomCardId() {
        // Генерируем 16-значный номер карты
        StringBuilder cardIdBuilder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardIdBuilder.append(random.nextInt(10));
        }
        return Long.parseLong(cardIdBuilder.toString());
    }

    String generateRandomCVV() {
        StringBuilder cvvBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            cvvBuilder.append(random.nextInt(10));
        }
        return cvvBuilder.toString();
    }

    String generateExpiryDate() {
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryDate = currentDate.plusYears(5);

        // Форматируем в MM/YY
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiryDate.format(formatter);
    }
}