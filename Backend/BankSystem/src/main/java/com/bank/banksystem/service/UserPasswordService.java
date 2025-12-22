package com.bank.banksystem.service;

import com.bank.banksystem.entity.User;
import com.bank.banksystem.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserPasswordService  {

    private final UserRepository userRepository;

    public UserPasswordService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * STEP 1: Check if entered password matches existing password
     */
    public boolean checkCurrentPassword(Long userId, String currentPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getUser_password().equals(currentPassword);
    }

    /**
     * STEP 2: Update password
     */
    public void updatePassword(Long userId, String newPassword) {

        userRepository.changePassword(userId, newPassword);
    }
}
