package com.bank.banksystem.service;

import com.bank.banksystem.dto.request.RegistrationRequest;
import com.bank.banksystem.dto.response.UserResponse;
import com.bank.banksystem.entity.User;
import com.bank.banksystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CashbackService cashbackService;

    public UserResponse register(RegistrationRequest registrationRequest) {
        try {
            // Data validation will be done in the frontend, no need for it here

            // Registrating user through repository
            userRepository.registerUser(
                    registrationRequest.getUser_email(),
                    registrationRequest.getUser_password(),
                    registrationRequest.getUser_name(),
                    registrationRequest.getUser_surname(),
                    registrationRequest.getUser_birthday(),
                    registrationRequest.getUser_salary(),
                    registrationRequest.getUser_id_card_no_series(),
                    registrationRequest.getUser_id_card_no(),
                    registrationRequest.getUser_fin(),
                    registrationRequest.getUser_phone_number(),
                    registrationRequest.getUser_codeword());

            // After that we log in the user
            User registeredUser = userRepository.login(
                    registrationRequest.getUser_email(),
                    registrationRequest.getUser_password());

            if (registeredUser == null) {
                throw new RuntimeException("User registration failed - cannot find registered user");
            }

            // Create cashback record for the new user
            cashbackService.createCashbackForUser(registeredUser);

            return convertToUserResponse(registeredUser);
            // error handling
        } catch (DataIntegrityViolationException e) {
            String email = registrationRequest.getUser_email();
            if (email == null) {
                throw new RuntimeException("Email is required");
            } else {
                throw new RuntimeException("Email already exists: " + email);
            }
        }
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getUser_id(),
                user.getUser_email(),
                user.getUser_name(),
                user.getUser_surname(),
                user.getUser_birthday(),
                user.getUser_salary(),
                user.getUser_id_card_no_series(),
                user.getUser_id_card_no(),
                user.getUser_fin(),
                user.getUser_phone_number(),
                user.getUser_codeword());
    }
}
