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

        } catch (DataIntegrityViolationException e) {
            handleDuplicateException(e, registrationRequest);
            throw new RuntimeException("Registration failed due to data integrity violation");
        }
    }

    private void handleDuplicateException(DataIntegrityViolationException e,
                                          RegistrationRequest request) {
        String rootMessage = e.getRootCause() != null ?
                e.getRootCause().getMessage().toLowerCase() : "";
        String fieldValue = "";
        String fieldName = "";

        // Проверка email
        if (rootMessage.contains("email") || rootMessage.contains("user_email")) {
            fieldName = "Email";
            fieldValue = request.getUser_email();
        }
        // Проверка FIN
        else if (rootMessage.contains("fin") || rootMessage.contains("user_fin")) {
            fieldName = "FIN";
            fieldValue = request.getUser_fin();
        }
        // Проверка телефонного номера
        else if (rootMessage.contains("phone") || rootMessage.contains("phone_number") ||
                rootMessage.contains("user_phone_number")) {
            fieldName = "Phone number";
            fieldValue = request.getUser_phone_number();
        }
        // Проверка ID карты
        else if (rootMessage.contains("id_card") || rootMessage.contains("idcardno") ||
                rootMessage.contains("user_id_card_no")) {
            fieldName = "ID Card number";
            fieldValue = request.getUser_id_card_no();
        }

        // Формируем сообщение об ошибке
        if (!fieldName.isEmpty()) {
            if (fieldValue == null || fieldValue.trim().isEmpty()) {
                throw new RuntimeException(fieldName + " is required");
            } else {
                throw new RuntimeException(fieldName + " already exists: " + fieldValue);
            }
        }

        // Если не нашли конкретное поле
        throw new RuntimeException("Registration failed. Please check your data");
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
