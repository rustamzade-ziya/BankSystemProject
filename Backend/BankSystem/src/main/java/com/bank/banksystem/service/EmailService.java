package com.bank.banksystem.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

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
                        "\n\nThis code is valid for a limited time."
        );
        mailSender.send(message);
    }

    // Send transfer/payment receipt
    public void sendReceiptEmail(String to, Long senderCardId, Long receiverCardId,
                                 String type, String amount, String fee) {

        // Mask card IDs (show only last 4 digits)
        String maskedSender = maskCard(senderCardId);
        String maskedReceiver = maskCard(receiverCardId);

        String messageText = String.format(
                "Transaction Type: %s\n" +
                        "Sender Card: %s\n" +
                        "Receiver Card: %s\n" +
                        "Amount: %s\n" +
                        "Fee: %s\n\n" +
                        "Thank you for using our bank.",
                type, maskedSender, maskedReceiver, amount, fee
        );

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Bank Transaction Receipt");
        message.setText(messageText);

        mailSender.send(message);
    }

    //  mask card number
    private String maskCard(Long cardId) {
        String cardStr = cardId.toString();
        int length = cardStr.length();
        if (length <= 4) {
            return "****" + cardStr; // in case card ID is very short
        }
        String last4 = cardStr.substring(length - 4);
        return "**** **** **** " + last4;
    }
}
