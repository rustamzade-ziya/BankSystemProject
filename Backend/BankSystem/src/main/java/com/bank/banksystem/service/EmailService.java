package com.bank.banksystem.service;

import com.bank.banksystem.repository.CreditCardRepository;
import com.bank.banksystem.repository.DebitCardRepository;
import com.bank.banksystem.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final DebitCardRepository debitCardRepository;
    private final UserRepository userRepository;
    private final CreditCardRepository creditCardRepository;

    public EmailService(JavaMailSender mailSender, DebitCardRepository debitCardRepository, UserRepository userRepository, CreditCardRepository creditCardRepository) {
        this.mailSender = mailSender;
        this.debitCardRepository = debitCardRepository;
        this.userRepository = userRepository;
        this.creditCardRepository = creditCardRepository;
    }

    public void sendOtpEmail(String to, int otpCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP code");
        message.setText(
                "Your one-time password is: " + otpCode +
                        "\n\nThis code is valid for a limited time.");
        mailSender.send(message);
    }

    // Send transfer/payment receipt
    public void sendReceiptEmail(
            String to,
            Long senderCardId,
            Long receiverCardId,
            String type,
            BigDecimal amount,
            BigDecimal convertedAmount,
            String senderCurrency,
            String receiverCurrency,
            BigDecimal fee) {

        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatted = dateTime.format(formatter);

        String messageText = "Transaction Type: " + type + "\n" +
                "Sender Card: " + maskCard(senderCardId) + "\n" +
                "Receiver Card: " + maskCard(receiverCardId) + "\n" +
                "Amount: " + amount + " " + senderCurrency + "\n" +
                "Converted Amount: " + convertedAmount + " " + receiverCurrency + "\n" +
                "Fee: " + fee + "\n" +
                "Time: " + formatted;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bank Transaction Receipt");
        message.setText(messageText);

        mailSender.send(message);

        if (debitCardRepository.existsByCardId(receiverCardId)) {
            Long userId = debitCardRepository.getIdbyCardId(receiverCardId);
            to = userRepository.findEmailbyId(userId);
            SimpleMailMessage message2 = new SimpleMailMessage();
            message2.setTo(to);
            message2.setSubject("Bank Transaction Receipt");
            message2.setText(messageText);
            mailSender.send(message2);
        } else if (creditCardRepository.existsByCardId(receiverCardId)) {
            Long userId = creditCardRepository.getIdbyCardId(receiverCardId);
            to = userRepository.findEmailbyId(userId);
            SimpleMailMessage message2 = new SimpleMailMessage();
            message2.setTo(to);
            message2.setSubject("Bank Transaction Receipt");
            message2.setText(messageText);
            mailSender.send(message2);
        }
    }

    // mask card number
    private String maskCard(Long cardId) {
        if (cardId == null) {
            return "N/A";
        }
        String cardStr = cardId.toString();
        int length = cardStr.length();
        if (length <= 4) {
            return "****" + cardStr; // in case card ID is very short
        }
        String last4 = cardStr.substring(length - 4);
        return "**** **** **** " + last4;
    }
}
