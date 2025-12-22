package com.bank.banksystem.service;

import com.bank.banksystem.dto.request.UpdateUserRequest;
import com.bank.banksystem.dto.response.UserResponse;
import com.bank.banksystem.entity.User;
import com.bank.banksystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChangeUserInformationService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse changeUserInformation(UpdateUserRequest request) {

        // Update profile (NO email, NO id)
        userRepository.updateProfile(
                request.getUser_id(),
                request.getUser_name(),
                request.getUser_surname(),
                request.getUser_salary(),
                request.getUser_birthday(),
                request.getUser_phone_number()
        );

        // Fetch updated user
        User updatedUser = userRepository.findById(request.getUser_id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return convertToUserResponse(updatedUser);
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getUser_id(),
                user.getUser_email(),
                user.getUser_name(),
                user.getUser_surname(),
                user.getUser_birthday(),
                user.getUser_salary(),
                user.getUser_phone_number());
    }
}

