package com.bank.banksystem.service;
import com.bank.banksystem.dto.request.LoginRequest;
import com.bank.banksystem.dto.response.UserResponse;
import com.bank.banksystem.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import com.bank.banksystem.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class LoginService {

    @Autowired
    private UserRepository userRepository;

    public UserResponse login(LoginRequest loginRequest) {
        //looking for the user in bank_db through userRepository's login method
        User user = userRepository.login(loginRequest.getEmail(), loginRequest.getPassword());
        //user not found error
        if (user == null) {
            throw new RuntimeException("Invalid email or password");
        }
        //if success - first convert all data to UserResponse, then return
        return convertToUserResponse(user);
    }
//for now both registration and login have this same code
    //in the future, if we need it, we can create a UserMapper class and move it there
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
