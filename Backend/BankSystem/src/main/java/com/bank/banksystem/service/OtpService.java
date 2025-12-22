package com.bank.banksystem.service;

import com.bank.banksystem.entity.Otp;
import com.bank.banksystem.repository.otpRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {

    private static final int OTP_TTL_MINUTES = 5;
    private static final int MAX_ATTEMPTS = 5;
    private static final int BLOCK_HOURS = 2;

    private final otpRepository otpRepository;

    public OtpService(otpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    // 📤 отправка OTP
    @Transactional
    public int sendOtp(String email) {

        LocalDateTime now = LocalDateTime.now();

        Otp otp = otpRepository.findByUserEmail(email)
                .orElse(new Otp());

        // 🔒 проверка блокировки
        if (otp.getBlockedUntil() != null && otp.getBlockedUntil().isAfter(now)) {
            throw new RuntimeException(
                    "User is blocked until " + otp.getBlockedUntil()
            );
        }

        int code = generateOtp();

        otp.setUserEmail(email);
        otp.setOtpCode(code);
        otp.setCreatedAt(now);
        // attempts НЕ трогаем
        // firstAttemptAt НЕ трогаем

        otpRepository.save(otp);
        return code;
    }

    // ✅ проверка OTP
    @Transactional
    public boolean verifyOtp(String email, int inputOtp) {

        Otp otp = otpRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        LocalDateTime now = LocalDateTime.now();

        // 🔒 блокировка
        if (otp.getBlockedUntil() != null && otp.getBlockedUntil().isAfter(now)) {
            throw new RuntimeException(
                    "User is blocked until " + otp.getBlockedUntil()
            );
        }

        // ⏰ TTL
        if (otp.getCreatedAt() == null ||
                otp.getCreatedAt().plusMinutes(OTP_TTL_MINUTES).isBefore(now)) {

            clearOtp(otp);
            throw new RuntimeException("OTP expired");
        }

        // первая попытка
        if (otp.getFirstAttemptAt() == null) {
            otp.setFirstAttemptAt(now);
            otp.setAttempts(0);
        }

        // окно 1 час
        if (otp.getFirstAttemptAt().plusHours(1).isBefore(now)) {
            otp.setAttempts(0);
            otp.setFirstAttemptAt(now);
        }

        // ❌ неверный код
        if (!otp.getOtpCode().equals(inputOtp)) {
            int attempts = otp.getAttempts() + 1;
            otp.setAttempts(attempts);

            if (attempts >= MAX_ATTEMPTS) {
                otp.setBlockedUntil(now.plusHours(BLOCK_HOURS));
            }

            otpRepository.save(otp);
            return false;
        }

        // ✅ успех
        otpRepository.deleteByUserEmail(email);
        return true;
    }

    private void clearOtp(Otp otp) {
        otp.setOtpCode(null);
        otp.setCreatedAt(null);
        otpRepository.save(otp);
    }

    private int generateOtp() {
        return 100000 + new Random().nextInt(900000);
    }
}