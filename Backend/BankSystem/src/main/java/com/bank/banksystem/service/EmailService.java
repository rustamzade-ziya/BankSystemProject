package com.bank.banksystem.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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

        String messageText = "Transaction Type: " + type + "\n" +
                "Sender Card: " + maskCard(senderCardId) + "\n" +
                "Receiver Card: " + maskCard(receiverCardId) + "\n" +
                "Amount: " + amount + " " + senderCurrency + "\n" +
                "Converted Amount: " + convertedAmount + " " + receiverCurrency + "\n" +
                "Fee: " + fee;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bank Transaction Receipt");
        message.setText(messageText);

        mailSender.send(message);
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
