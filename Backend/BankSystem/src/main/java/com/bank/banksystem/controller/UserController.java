package com.bank.banksystem.controller;

import com.bank.banksystem.dto.request.UpdateUserRequest;
import com.bank.banksystem.dto.response.UserResponse;
import com.bank.banksystem.service.ChangeUserInformationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/dashboard/user-settings")
public class UserController {

    @Autowired
    private ChangeUserInformationService changeUserInformationService;

    @PutMapping("/change")
    public UserResponse changeUserInformation(@RequestBody UpdateUserRequest request) {
        return changeUserInformationService.changeUserInformation(request);
    }
}

