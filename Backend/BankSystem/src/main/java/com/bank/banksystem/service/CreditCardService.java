package com.bank.banksystem.service;

import com.bank.banksystem.dto.request.CreateCreditCardRequest;
import com.bank.banksystem.dto.response.CreateCreditCardResponse;
import com.bank.banksystem.repository.CreditCardRepository;
import com.bank.banksystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class CreditCardService {

    @Autowired
    private CreditCardRepository creditCardRepository;

    @Autowired
    private UserRepository userRepository;

    private final Random random = new Random();
    private final BigDecimal DEFAULT_INTEREST_RATE = new BigDecimal("15.00"); // 15% по умолчанию

    public CreateCreditCardResponse createCreditCard(CreateCreditCardRequest request) {
        // Проверяем существование пользователя
        if (!userRepository.existsById(request.getUserId())) {
            return new CreateCreditCardResponse(null, null, null, null, null, null, "User not found");
        }

        // Генерируем уникальный cardId
        Long cardId;
        do {
            cardId = generateRandomCardId();
        } while (creditCardRepository.existsByCardId(cardId) ||
                creditCardRepository.existsInDebitCards(cardId));

        // Генерируем CVV
        String cvv = generateRandomCVV();

        // Генерируем expiry date (текущая дата + 5 лет)
        String expiryDate = generateExpiryDate();

        // Рассчитываем процентную ставку на основе loanAmount
        BigDecimal interestRate = calculateInterestRate(request.getLoanAmount());
        BigDecimal loanAmount = request.getLoanAmount();
        // Создаем карту в базе данных
        try {
            creditCardRepository.insertCard(
                    cardId,
                    request.getUserId(),
                    loanAmount.doubleValue(), // начальный баланс (использованный кредит)
                    loanAmount.doubleValue(), // лимит кредита
                    interestRate.floatValue(), // процентная ставка
                    request.getPpn(),
                    request.getCurrency(),
                    cvv,
                    expiryDate,
                    "ACTIVE",
                    request.getPin()
            );

            return new CreateCreditCardResponse(
                    cardId,
                    cvv,
                    expiryDate,
                    "ACTIVE",
                    interestRate,
                    loanAmount,
                    "Credit card created successfully"
            );
        } catch (Exception e) {
            return new CreateCreditCardResponse(
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    "Failed to create credit card: " + e.getMessage()
            );
        }
    }

    private Long generateRandomCardId() {
        // Генерируем 16-значный номер карты
        StringBuilder cardIdBuilder = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            cardIdBuilder.append(random.nextInt(10));
        }
        return Long.parseLong(cardIdBuilder.toString());
    }

    private String generateRandomCVV() {
        StringBuilder cvvBuilder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            cvvBuilder.append(random.nextInt(10));
        }
        return cvvBuilder.toString();
    }

    private String generateExpiryDate() {
        LocalDate currentDate = LocalDate.now();
        LocalDate expiryDate = currentDate.plusYears(5);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
        return expiryDate.format(formatter);
    }

    private BigDecimal calculateInterestRate(BigDecimal loanAmount) {
        // Логика расчета процентной ставки:
        // Чем больше loanAmount, тем ниже процентная ставка
        if (loanAmount.compareTo(new BigDecimal("10000")) >= 0) {
            return new BigDecimal("12.50"); // 12.5% для больших сумм
        } else if (loanAmount.compareTo(new BigDecimal("5000")) >= 0) {
            return new BigDecimal("13.50"); // 13.5% для средних сумм
        } else {
            return DEFAULT_INTEREST_RATE; // 15% по умолчанию
        }
    }
}