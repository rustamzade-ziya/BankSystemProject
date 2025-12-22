package com.bank.banksystem.service;

import com.bank.banksystem.dto.request.RegistrationCheckRequest;
import com.bank.banksystem.dto.response.RegistrationCheckResponse;
import com.bank.banksystem.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrationCheckService {

    private final UserRepository userRepository;

    public RegistrationCheckService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public RegistrationCheckResponse checkUniqueFields(RegistrationCheckRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            return new RegistrationCheckResponse(false, "email");
        }

        if (userRepository.existsByIdCardNo(request.getIdCardNo())) {
            return new RegistrationCheckResponse(false, "idCardNo");
        }

        if (userRepository.existsByFin(request.getFin())) {
            return new RegistrationCheckResponse(false, "fin");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            return new RegistrationCheckResponse(false, "phoneNumber");
        }

        return new RegistrationCheckResponse(true);
    }
}
